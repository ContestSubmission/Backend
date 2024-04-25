package com.github.contestsubmission.backend.feature.team

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.feature.team.dto.EnumeratedTeamDTO
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.NoResultException
import jakarta.transaction.Transactional
import java.util.*

@ApplicationScoped
class TeamRepository : CRUDRepository<Team, UUID>(Team::class.java) {
	override val entityName = Team.ENTITY_NAME

	fun listByContest(contest: Contest): List<EnumeratedTeamDTO> =
		entityManager.createQuery("SELECT NEW com.github.contestsubmission.backend.feature.team.dto.EnumeratedTeamDTO(t.id, t.name, t.owner, size(t.members), size(t.submissions)) FROM Team t WHERE t.contest = :contest", EnumeratedTeamDTO::class.java)
			.setParameter("contest", contest)
			.resultList
			?: emptyList()

	@Transactional
	fun addUserToTeam(user: Person, team: Team) {
		team.members.add(user)
		// no clue if you need this, firstly, it only worked WITHOUT it, now, it only works WITH it
		entityManager.merge(team)
	}

	fun getTeamByMember(contest: Contest, person: Person): Team? = try {
		entityManager.createQuery("""
				SELECT t FROM Contest c
				JOIN c.teams t
				WHERE c.id = :contestId AND :person MEMBER OF t.members
			""".trimIndent(), Team::class.java)
			.setParameter("contestId", contest.id)
			.setParameter("person", person)
			.singleResult
	} catch (ignored: NoResultException) {
		null
	}

	fun canJoinTeam(contest: Contest, caller: Person): Boolean
		= getTeamByMember(contest, caller) == null && contest.organizer != caller
}
