package com.github.contestsubmission.backend.feature.user

import io.quarkus.security.Authenticated
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path

@Path("/person")
class PersonResource {

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@POST
	@Authenticated
	/**
	 * Creates a person from the OIDC provider in the DB
	 * This is required because there is no automatic sync in place
	 * Will be called by either the frontend on signup or a webhook in the IAM system
	 */
	open suspend fun createPerson() {
		val uuid = userAuthenticationService.getUUID()
		userAuthenticationService.getUserByCallerId(uuid) ?: userAuthenticationService.createUser(Person(uuid))
	}
}
