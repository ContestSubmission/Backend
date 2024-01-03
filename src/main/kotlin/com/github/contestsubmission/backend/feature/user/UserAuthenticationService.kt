package com.github.contestsubmission.backend.feature.user

import com.github.contestsubmission.backend.util.toUUID
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
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
}
