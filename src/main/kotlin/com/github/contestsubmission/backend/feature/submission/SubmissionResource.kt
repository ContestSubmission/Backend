package com.github.contestsubmission.backend.feature.submission

import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import io.github.dyegosutil.awspresignedpost.conditions.key.KeyStartingWithCondition
import io.github.dyegosutil.awspresignedpost.postparams.PostParams
import io.github.dyegosutil.awspresignedpost.signer.S3PostSigner
import io.quarkus.security.Authenticated
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.regions.Region
import java.time.Clock.systemUTC
import java.time.Instant.now
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


// will be replaced with a configurable max file size at some point
const val MAX_FILE_SIZE: Long = 100 * 1024 * 1024 // 100 MB

@Path("/contest/{contestId}/submission")
class SubmissionResource {

	@ConfigProperty(name = "s3.bucket")
	lateinit var bucket: String

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@PathParam("contestId")
	lateinit var contestId: UUID

	@ConfigProperty(name = "quarkus.s3.endpoint-override")
	lateinit var endpointOverride: String

	data class PreSignedPost(val url: String, val conditions: Map<String, String>)

	@Path("/{teamId}/get_presigned_url")
	@GET
	@Authenticated
	@Produces(MediaType.APPLICATION_JSON)
	fun create(@PathParam("teamId") teamId: UUID): PreSignedPost? {
		val team = userAuthenticationService.getTeam(teamId, contestId)

		val now = now(systemUTC())

		if (now >= team.contest.deadline.toInstant(UTC)) {
			throw BadRequestException("Contest has already ended!")
		}

		val oneMinuteFromNow: ZonedDateTime = now.plusSeconds(60).atZone(UTC)
		val postParams = PostParams
			.builder(
				Region.of("eu-frankfurt-1"),
				oneMinuteFromNow,
				bucket,
				KeyStartingWithCondition("submissions/${contestId}/${team.id}/${DateTimeFormatter.ISO_DATE_TIME.withZone(UTC).format(now)}_"),
			)
			.withContentLengthRange(1, MAX_FILE_SIZE) // file size upload limit in bytes
			.build()
		val conditions = S3PostSigner.sign(postParams).conditions
		return PreSignedPost("$endpointOverride/$bucket", conditions)
	}
}
