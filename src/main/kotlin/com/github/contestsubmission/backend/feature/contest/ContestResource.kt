package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import com.github.contestsubmission.backend.util.db.findByIdFullFetch
import com.github.contestsubmission.backend.util.rest.UriBuildable
import com.github.contestsubmission.backend.util.rest.response
import io.quarkus.security.Authenticated
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import java.util.*

@Path("/contest")
class ContestResource : UriBuildable {

	@Inject
	lateinit var contestRepository: ContestRepository

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@Context
	override lateinit var uriInfo: UriInfo

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	open suspend fun createContest(@Valid contest: Contest): Response {
		val caller = userAuthenticationService.getUser() ?: return Response.status(Response.Status.UNAUTHORIZED).build()

		contest.organizer = caller
		contest.id = null
		return contestRepository.persist(contest).response {
			header("Location", contest.id.toUri())
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	open suspend fun getContest(@Valid id: UUID): Response {
		return contestRepository.findByIdFullFetch(id)?.response() ?: Response.status(Response.Status.NOT_FOUND).build()
	}
}
