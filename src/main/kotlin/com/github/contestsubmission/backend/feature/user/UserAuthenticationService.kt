package com.github.contestsubmission.backend.feature.user

import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.team.TeamRepository
import com.github.contestsubmission.backend.util.entityNotFound
import com.github.contestsubmission.backend.util.findById
import com.github.contestsubmission.backend.util.toUUID
import io.quarkus.logging.Log
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*

@RequestScoped
class UserAuthenticationService {
	@Inject
	protected lateinit var userRepository: PersonRepository

	fun getOrCreatePerson(callerId: UUID): Person? {
		return userRepository.findByIdOrNull(callerId) ?: createUser(Person(callerId))
	}

	@Inject
	protected lateinit var jwt: JsonWebToken

	fun getUserOrNull(): Person? = getOrCreatePerson(getUUID())

	fun getUUID() = jwt.claim<String>("sub").orElseThrow { IllegalStateException("JWT does not contain an id") }
		.toUUID() ?: throw IllegalStateException("id is not a valid UUID")

	fun getEmail(): String? = jwt.getClaim("email")

	fun createUser(person: Person): Person {
		Log.info("Creating person record for ${person.id}")
		return userRepository.persist(person)
	}

	@Inject
	protected lateinit var teamRepository: TeamRepository

	fun getTeam(teamId: UUID, contestId: UUID? = null): Team {
		val user = getUserOrNull() ?: throw NotFoundException("User not found")

		val team = teamRepository.findById(teamId)

		if (contestId != null && team.contest.id != contestId) {
			throw NotFoundException(entityNotFound(Team.ENTITY_NAME))
		}

		if (!team.members.contains(user)) {
			throw ForbiddenException("User is not a member of this team")
		}

		return team
	}
}
