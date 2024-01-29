package com.github.contestsubmission.backend.feature.submission

import com.github.contestsubmission.backend.feature.submission.dto.HandInSubmissionDTO
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import com.github.contestsubmission.backend.util.expiresIn
import com.github.contestsubmission.backend.util.toUUID
import io.github.dyegosutil.awspresignedpost.conditions.key.ExactKeyCondition
import io.github.dyegosutil.awspresignedpost.postparams.PostParams
import io.github.dyegosutil.awspresignedpost.signer.S3PostSigner
import io.quarkus.security.Authenticated
import io.quarkus.security.UnauthorizedException
import io.smallrye.jwt.auth.principal.JWTParser
import io.smallrye.jwt.build.Jwt
import jakarta.inject.Inject
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.regions.Region
import java.net.URL
import java.time.Clock.systemUTC
import java.time.Instant.now
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.Duration.Companion.hours


// will be replaced with a configurable max file size at some point
const val MAX_FILE_SIZE: Long = 100 * 1024 * 1024 // 100 MB

@Path("/contest/{contestId}/submission/{teamId}")
class SubmissionResource {

	@ConfigProperty(name = "s3.bucket")
	lateinit var bucket: String

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@PathParam("contestId")
	lateinit var contestId: UUID

	@PathParam("teamId")
	lateinit var teamId: UUID

	@ConfigProperty(name = "quarkus.s3.endpoint-override")
	lateinit var endpointOverride: String

	data class PreSignedPost(val url: String, val conditions: Map<String, String>, val jwt: String)

	@Path("get_presigned_url")
	@GET
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	fun create(@QueryParam("fileName") @NotNull @NotBlank fileName: String): PreSignedPost? {
		val user = userAuthenticationService.getUser() ?: throw UnauthorizedException("You are not logged in!")
		val team = userAuthenticationService.getTeam(teamId, contestId)

		val now = now(systemUTC())

		if (now >= team.contest.deadline.toInstant(UTC)) {
			throw BadRequestException("Contest has already ended!")
		}

		val oneMinuteFromNow: ZonedDateTime = now.plusSeconds(60).atZone(UTC)
		val fullFileName = "submissions/${contestId}/${team.id}/${DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).format(now)}_$fileName"
		val postParams = PostParams
			.builder(
				Region.of("eu-frankfurt-1"),
				oneMinuteFromNow,
				bucket,
				ExactKeyCondition(
					fullFileName
				)
			)
			.withContentLengthRange(1, MAX_FILE_SIZE) // file size upload limit in bytes
			.build()
		val conditions = S3PostSigner.sign(postParams).conditions

		val baseUrl = "$endpointOverride/$bucket"
		val jwt = Jwt.claims().apply {
			subject(user.id.toString())
			claim("team", team.id.toString())
			claim("fileName", "$baseUrl/$fullFileName")
			expiresIn(1.hours)
		}.sign()

		return PreSignedPost(baseUrl, conditions, jwt)
	}

	@Inject
	lateinit var jwtParser: JWTParser

	@Inject
	lateinit var submissionRepository: SubmissionRepository

	@Path("submit")
	@POST
	@Authenticated
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	fun submit(handInSubmissionDTO: HandInSubmissionDTO): Submission {
		val user = userAuthenticationService.getUser() ?: throw UnauthorizedException("You are not logged in!")
		val team = userAuthenticationService.getTeam(teamId, contestId)

		val jwt = try {
			jwtParser.parse(handInSubmissionDTO.jwt)
		} catch (e: Exception) {
			throw BadRequestException("Illegal request - invalid JWT: ${e.message}")
		}

		val userId = jwt.subject?.toUUID() ?: throw BadRequestException("Invalid subject")
		if (userId != user.id) {
			throw BadRequestException("Illegal request - mismatched user")
		}
		val teamId = jwt.claim<String>("team").orElseThrow { BadRequestException("Invalid team") }.toUUID()
		if (teamId != team.id) {
			throw BadRequestException("Illegal request - mismatched team")
		}

		val fileName = jwt.claim<String>("fileName").orElseThrow { BadRequestException("Illegal request - invalid fileName") }
		val passedURL = URL(handInSubmissionDTO.url)
		val uploadedURL = URL(fileName)
		val endpoint = URL(endpointOverride)
		if (passedURL.file != uploadedURL.file || passedURL.host != endpoint.host || passedURL.port != endpoint.port) {
			throw BadRequestException("Illegal request - mismatched file name or host. passedURL: $passedURL, uploadedURL: $uploadedURL, endpoint: $endpoint")
		}

		val now = now(systemUTC())

		if (now >= team.contest.deadline.toInstant(UTC)) {
			throw BadRequestException("Contest has already ended!")
		}

		val submission = handInSubmissionDTO.toEntity().apply {
			this.url = passedURL.toExternalForm()
			this.team = team
			this.handedInAt = now
			this.uploadedBy = user
		}
		submissionRepository.persist(submission)

		return submission
	}
}
