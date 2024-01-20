package com.github.contestsubmission.backend.feature.user

import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.team.TeamRepository
import com.github.contestsubmission.backend.util.toUUID
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*

@RequestScoped
class UserAuthenticationService {
	@Inject
	lateinit var userRepository: PersonRepository

	fun getUserByCallerId(callerId: UUID): Person? {
		return userRepository.findById(callerId)
	}

	@Inject
	lateinit var jwt: JsonWebToken

	fun getUser(): Person? = getUserByCallerId(getUUID())

	fun getUUID() = jwt.claim<String>("sub").orElseThrow { IllegalStateException("JWT does not contain an id") }
		.toUUID() ?: throw IllegalStateException("id is not a valid UUID")

	fun createUser(person: Person): Person {
		return userRepository.persist(person)
	}

	@Inject
	lateinit var teamRepository: TeamRepository

	fun getTeam(teamId: UUID, contestId: UUID? = null): Team {
		val user = getUser() ?: throw NotFoundException("User not found")

		val team = teamRepository.findById(teamId) ?: throw NotFoundException("Team not found")

		if (contestId != null && team.contest.id != contestId) {
			throw NotFoundException("Team not found")
		}

		if (!team.members.contains(user)) {
			throw ForbiddenException("User is not a member of this team")
		}

		return team
	}
}
