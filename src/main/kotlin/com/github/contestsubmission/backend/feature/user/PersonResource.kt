package com.github.contestsubmission.backend.feature.user

import io.quarkus.security.Authenticated
import io.smallrye.common.annotation.Blocking
import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.inject.Inject
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path

@Path("/person")
@RunOnVirtualThread
@Blocking
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
	fun createPerson() {
		val uuid = userAuthenticationService.getUUID()
		userAuthenticationService.getUserByCallerId(uuid) ?: userAuthenticationService.createUser(Person(uuid))
	}
}
