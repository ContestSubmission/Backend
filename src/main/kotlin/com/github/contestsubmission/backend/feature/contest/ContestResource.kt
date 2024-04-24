package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.contest.dto.ContestCreateDTO
import com.github.contestsubmission.backend.feature.contest.dto.ParticipatedContestDTO
import com.github.contestsubmission.backend.feature.contest.dto.PersonalContestDTO
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import com.github.contestsubmission.backend.util.db.findByIdFullFetch
import com.github.contestsubmission.backend.util.rest.UriBuildable
import com.github.contestsubmission.backend.util.rest.response
import io.quarkus.security.Authenticated
import io.quarkus.security.UnauthorizedException
import io.smallrye.common.annotation.Blocking
import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.UriInfo
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import java.time.Instant
import java.util.*

@Path("/contest")
@RunOnVirtualThread
@Blocking
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
	@APIResponse(
		responseCode = "200", description = "Contest created", content = [Content(
			mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = Contest::class)
		)]
	)
	fun createContest(@Valid contestCreateDTO: ContestCreateDTO): Response {
		val caller = userAuthenticationService.getUser() ?: return Response.status(Response.Status.UNAUTHORIZED).build()
		val contest = contestCreateDTO.toEntity()
		contest.organizer = caller

		return contestRepository.persist(contest).response {
			header("Location", contest.id.toUri())
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(responseCode = "404", description = "Contest not found")
	@APIResponse(
		responseCode = "200", description = "Contest found", content = [Content(
			mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = Contest::class)
		)]
	)
	fun getContest(@Valid id: UUID) = contestRepository.findByIdFullFetch(id).response()

	@GET
	@Path("/{id}/personal")
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(responseCode = "404", description = "Contest not found")
	@APIResponse(
		responseCode = "200", description = "Contest found", content = [Content(
			mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = PersonalContestDTO::class)
		)]
	)
	@Authenticated
	fun getPersonalContest(@Valid id: UUID): PersonalContestDTO {
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")

		return contestRepository.getPersonalContest(caller, id) ?: throw NotFoundException("Contest not found")
	}

	@PUT
	@Path("/{id}/endNow")
	@Authenticated
	fun endNow(@Valid id: UUID) {
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")
		val contest = contestRepository.findById(id) ?: throw NotFoundException("Contest not found")

		if (contest.organizer != caller) {
			throw UnauthorizedException("You are not the organizer of this contest")
		}

		contest.deadline = Instant.now()
		contestRepository.merge(contest)
	}

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(
		responseCode = "200", description = "Contests found (or not)", content = [Content(
			mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = Contest::class, type = SchemaType.ARRAY)
		)]
	)
	fun listContests(@QueryParam("term") term: String?): Response = contestRepository.search(term ?: "").response()

	@GET
	@Path("/my")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	fun myContests(): List<ParticipatedContestDTO> {
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")

		return contestRepository.findParticipatedContests(caller)
	}
}
