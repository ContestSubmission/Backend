package com.github.contestsubmission.backend.feature.team

import com.github.contestsubmission.backend.feature.contest.ContestRepository
import com.github.contestsubmission.backend.feature.team.dto.EnumeratedTeamDTO
import com.github.contestsubmission.backend.feature.team.dto.TeamCreateDTO
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
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import java.util.*

@Path("/contest/{contestId}/team")
@RunOnVirtualThread
@Blocking
class TeamResource : UriBuildable {

	@Context
	override lateinit var uriInfo: UriInfo

	@PathParam("contestId")
	lateinit var contestId: UUID

	@Inject
	lateinit var teamRepository: TeamRepository

	@Inject
	lateinit var contestRepository: ContestRepository

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Authenticated
	@APIResponse(
		responseCode = "200", description = "Team created", content = [Content(
			mediaType = MediaType.APPLICATION_JSON, schema = Schema(implementation = Team::class)
		)]
	)
	fun create(@Valid teamCreateDTO: TeamCreateDTO): Response {
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")
		val contest = contestRepository.findById(contestId) ?: throw NotFoundException("Contest not found")

		val team = teamCreateDTO.toEntity()
		team.contest = contest
		team.owner = caller
		team.members.add(caller)

		return teamRepository.persist(team).response {
			header("Location", team.id.toUri())
		}
	}

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	fun list(): List<EnumeratedTeamDTO> {
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")
		val contest = contestRepository.findById(contestId) ?: throw NotFoundException("Contest not found")
		// will be replaced by role-based authorization later
		if (caller.id == contest.organizer?.id) {
			return teamRepository.listByContest(contest)
		}
		throw ForbiddenException("Only the organizer can list teams")

	}

	@GET
	@Path("{teamId}/get")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	fun get(@PathParam("teamId") teamId: UUID): Team {
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")
		val team = teamRepository.findByIdFullFetch(teamId) ?: throw NotFoundException("Team not found")

		if (team.contest.id != contestId) {
			throw NotFoundException("Team not found")
		}

		if (team.members.any { it.id == caller.id } || team.contest.organizer?.id == caller.id) {
			return team
		}

		throw ForbiddenException("Only the organizer and team members can view a team")
	}
}
