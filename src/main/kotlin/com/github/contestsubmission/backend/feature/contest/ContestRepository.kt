package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO
import com.github.contestsubmission.backend.feature.contest.dto.PersonalContestDTO
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import org.hibernate.Hibernate
import java.util.*

@ApplicationScoped
class ContestRepository : CRUDRepository<Contest, UUID>(Contest::class) {

	fun search(term: String): MutableList<Contest>? =
		entityManager.createQuery("""
			SELECT c FROM Contest c WHERE
				(lower(c.name) LIKE lower(:term) OR lower(c.description) LIKE lower(:term))
				AND c.public
		""".trimIndent(), Contest::class.java)
			.setParameter("term", "%$term%")
			.resultList

	fun findParticipatedContests(caller: Person): List<ParticipatedContestDTO> =
		entityManager.createQuery("""
			SELECT DISTINCT
				NEW com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO(
					c.id,
					c.name,
					(c.organizer = :caller),
					t
			) FROM Contest c
			LEFT JOIN c.teams t
			LEFT JOIN t.members m
			WHERE c.organizer = :caller OR m = :caller
		""".trimIndent(), ParticipatedContestDTO::class.java)
			.setParameter("caller", caller)
			.resultList

	fun getPersonalContest(caller: Person, contestId: UUID): PersonalContestDTO? =
		entityManager.createQuery(
			"""
			SELECT NEW com.github.contestsubmission.backend.feature.contest.dto.PersonalContestDTO(
				c.name,
				c.organizer,
				c.description,
				c.deadline,
				c.maxTeamSize,
				t
			)
			FROM Contest c
			LEFT JOIN c.teams t
			LEFT JOIN FETCH t.members m
			WHERE c.id = :contestId AND (c.organizer = :caller OR m = :caller)
		""".trimIndent(),
			PersonalContestDTO::class.java
		)
			.setParameter("caller", caller)
			.setParameter("contestId", contestId)
			.singleResult
			// epic hack to make sure that the team is null if the caller is the organizer
			// this tells the client that the user cannot submit something
			?.run { if (organizer == caller) copy(team = null) else this }
}
