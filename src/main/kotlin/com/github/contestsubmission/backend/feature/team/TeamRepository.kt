package com.github.contestsubmission.backend.feature.team

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class TeamRepository : CRUDRepository<Team, UUID>() {
	override val entityClass: Class<Team> = Team::class.java

	fun findByContest(contest: Contest): List<Team> =
		entityManager.createQuery("SELECT t FROM Team t WHERE t.contest = :contest", Team::class.java)
			.setParameter("contest", contest)
			.resultList
			?: emptyList()
}
