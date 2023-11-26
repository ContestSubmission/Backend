package com.github.contestsubmission.backend.feature.user

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.UUID

@ApplicationScoped
class UserAuthenticationService {
	@Inject
	lateinit var userRepository: PersonRepository

	// will be replaced by OIDC later - see https://github.com/ContestSubmission/Backend/issues/4
	open suspend fun getUserByCallerId(callerId: UUID): Person? {
		return userRepository.findById(callerId)
	}
}
