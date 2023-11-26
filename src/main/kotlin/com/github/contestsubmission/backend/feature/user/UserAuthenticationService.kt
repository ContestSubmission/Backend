package com.github.contestsubmission.backend.feature.user

import com.github.contestsubmission.backend.util.toUUID
import io.quarkus.oidc.IdToken
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*

@RequestScoped
class UserAuthenticationService {
	@Inject
	lateinit var userRepository: PersonRepository

	// will be replaced by OIDC later - see https://github.com/ContestSubmission/Backend/issues/4
	open suspend fun getUserByCallerId(callerId: UUID): Person? {
		return userRepository.findById(callerId)
	}

	@Inject
	@IdToken
	lateinit var jwt: JsonWebToken

	open suspend fun getUser(): Person? = getUserByCallerId(getUUID())

	fun getUUID() = jwt.claim<String>("sub").orElseThrow { IllegalStateException("JWT does not contain an id") }
		.toUUID() ?: throw IllegalStateException("id is not a valid UUID")

	open suspend fun createUser(person: Person): Person {
		return userRepository.persist(person)
	}
}
