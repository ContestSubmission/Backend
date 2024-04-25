package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO
import com.github.contestsubmission.backend.feature.contest.dto.PersonalContestDTO
import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class ContestRepository : CRUDRepository<Contest, UUID>(Contest::class) {
	override val entityName = Contest.ENTITY_NAME

	fun search(term: String): MutableList<Contest>? =
		entityManager.createQuery(
			"""
			SELECT c FROM Contest c WHERE
				(lower(c.name) LIKE lower(:term) OR lower(c.description) LIKE lower(:term))
				AND c.publicAccessible
		""".trimIndent(), Contest::class.java)
			.setParameter("term", "%$term%")
			.resultList

	fun findParticipatedContests(caller: Person): List<ParticipatedContestDTO> {
		val contestsAsOrganizer = entityManager.createQuery("""
			SELECT NEW com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO(
				c.id,
				c.name,
				c.deadline,
				true
			)
			FROM Contest c
			WHERE c.organizer = :caller
		""".trimIndent(), ParticipatedContestDTO::class.java)
			.setParameter("caller", caller)
			.resultList

		val contestsAsParticipant = entityManager.createQuery("""
			SELECT NEW com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO(
				c.id,
				c.name,
				c.deadline,
				false,
				t
			)
			FROM Contest c
			JOIN c.teams t
			JOIN t.members m
			WHERE m = :caller AND c.organizer != :caller
		""".trimIndent(), ParticipatedContestDTO::class.java)
			.setParameter("caller", caller)
			.resultList

		return contestsAsOrganizer + contestsAsParticipant
	}

	// cast from MutableList -> List
	@Suppress("UNCHECKED_CAST", "kotlin:S6531")
	fun getPersonalContestOrNull(caller: Person, contestId: UUID): PersonalContestDTO? {
		val personalContestDTO = entityManager.createQuery(
			"""
				SELECT NEW com.github.contestsubmission.backend.feature.contest.dto.PersonalContestDTO(
					c.id,
					c.name,
					c.organizer,
					c.description,
					c.publicGrading,
					c.deadline,
					c.maxTeamSize,
					t
				)
				FROM Contest c
				LEFT JOIN c.teams t ON :caller MEMBER OF t.members
				WHERE c.id = :contestId
			""".trimIndent(),
			PersonalContestDTO::class.java
		)
			.setParameter("caller", caller)
			.setParameter("contestId", contestId)
			.resultList
			.firstOrNull()

		// user isn't in a team => no submissions
		if (personalContestDTO?.team == null) return personalContestDTO

		val submissions: List<Submission> = entityManager.createQuery(
			"""
				SELECT s
				FROM Submission s
				WHERE s.team.id = :teamId
				ORDER BY s.handedInAt DESC
			""".trimIndent(),
		).setParameter("teamId", personalContestDTO.team.id)
			.resultList as List<Submission>

		personalContestDTO.submissions = submissions

		return personalContestDTO
	}
}
