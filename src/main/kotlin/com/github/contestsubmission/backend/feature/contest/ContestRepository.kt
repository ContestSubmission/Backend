package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
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
}
