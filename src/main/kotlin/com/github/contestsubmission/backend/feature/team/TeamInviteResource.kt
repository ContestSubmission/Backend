package com.github.contestsubmission.backend.feature.team

import com.github.contestsubmission.backend.feature.mail.MailerService
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import com.github.contestsubmission.backend.util.expiresIn
import com.github.contestsubmission.backend.util.toUUID
import io.quarkus.security.Authenticated
import io.quarkus.security.UnauthorizedException
import io.smallrye.jwt.auth.principal.JWTParser
import io.smallrye.jwt.build.Jwt
import jakarta.inject.Inject
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestQuery
import java.util.*
import kotlin.time.Duration.Companion.hours

// this should be a subresource, however, QUARKUS DOENS'T WORK AGAIN and fucks up the dependency injection
@Path("/")
class TeamInviteResource {
	@Inject
	lateinit var jwtParser: JWTParser

	@Inject
	lateinit var teamRepository: TeamRepository

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@Path("invite/accept")
	@POST
	@Authenticated
	fun accept(@RestQuery("invite") @NotNull invite: String): Response {
		val jwt = jwtParser.parse(invite)
		val teamId = jwt.getClaim<String>("teamId").toUUID() ?: throw InternalServerErrorException("JWT contains invalid UUID!")
		val team = teamRepository.findById(teamId)
			?: throw NotFoundException("Team not found")

		if (team.members.size >= team.contest.maxTeamSize) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Team is already full!").build()
		}

		val person = userAuthenticationService.getUser() ?: throw UnauthorizedException("You are not logged in!")

		if (jwt.subject != userAuthenticationService.getEmail()) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity("Mismatched mail address - make sure your primary mail is the same as the invited one!")
				.build()
		}

		if (!teamRepository.canJoinTeam(team.contest, person)) {
			return Response.status(Response.Status.CONFLICT).entity("User cannot join a team!").build()
		}

		teamRepository.addUserToTeam(person, team)

		return Response.noContent().build()
	}

	@Inject
	lateinit var mailerService: MailerService

	@Path("{contestId}/team/{teamId}/invite/create")
	@POST
	@Authenticated
	fun create(@PathParam("contestId") @NotNull contestId: UUID, @PathParam("teamId") @NotNull teamId: UUID, @RestQuery("email") @NotNull email: String): Response {
		val person = userAuthenticationService.getUser() ?: throw UnauthorizedException("You are not logged in!")
		val team = teamRepository.findById(teamId) ?: throw NotFoundException("Team not found")
		if (team.contest.id != contestId) {
			throw NotFoundException("Team not found")
		}

		if (!team.members.contains(person)) {
			throw ForbiddenException("You are not a member of this team!")
		}

		if (team.members.size >= team.contest.maxTeamSize) {
			throw ForbiddenException("Team is already full!")
		}

		val jwt = Jwt.claims().apply {
			subject(email)
			claim("contestId", contestId)
			claim("teamId", teamId)
			expiresIn(24.hours)
		}.sign()

		mailerService.sendInviteMail(email, team, jwt)
			.await()
			.indefinitely()

		// the mail service is async, so we can return immediately
		return Response.accepted().build()
	}
}
