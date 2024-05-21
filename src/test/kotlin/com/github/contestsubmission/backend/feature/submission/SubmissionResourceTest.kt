package com.github.contestsubmission.backend.feature.submission

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.feature.submission.dto.HandInSubmissionDTO
import com.github.contestsubmission.backend.feature.team.StaticTeamRepository
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.team.TeamRepository
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.*

@QuarkusTest
class SubmissionResourceTest {
	@Inject
	lateinit var jwtParser: JWTParser

	@InjectMock
	lateinit var submissionRepository: SubmissionRepository

	lateinit var submissionResource: SubmissionResource

	@BeforeEach
	fun setup() {
		submissionResource = SubmissionResource().apply {
			jwtParser = this@SubmissionResourceTest.jwtParser
			userAuthenticationService = object : UserAuthenticationService() {
				override fun getUserOrNull() = PERSON
				override var teamRepository: TeamRepository = StaticTeamRepository(TEAM)
			}
			teamId = UUID.fromString(TEAM_UUID)
			contestId = UUID.fromString(CONTEST_UUID)
			endpointOverride = BASE_S3_URL
			submissionRepository = this@SubmissionResourceTest.submissionRepository
		}
	}

	companion object {
		const val USER_UUID = "c77f09f3-3b4d-4608-bafc-8c7a31a7b559"
		val PERSON = Person(UUID.fromString(USER_UUID))
		const val CONTEST_UUID = "a77f09f3-3b4d-4608-bafc-8c7a31a7b559"
		val CONTEST = Contest(UUID.fromString(CONTEST_UUID), "Contest")
		const val TEAM_UUID = "b77f09f3-3b4d-4608-bafc-8c7a31a7b559"
		val TEAM = Team(UUID.fromString(TEAM_UUID), "Team", contest = CONTEST, members = mutableSetOf(PERSON))

		const val BASE_S3_URL = "https://s3.contestsubmission.example.com"
		const val BASE_FILE_PATH = "submissions/$CONTEST_UUID/$TEAM_UUID/2024-01-01T01:01:01.111111111Z_"
		const val FILE_NAME = "my_file.png"
		const val FILE_NAME_SPACE = "my file.png"
		const val FULL_FILE_NAME = "$BASE_FILE_PATH$FILE_NAME"
		const val FULL_FILE_NAME_SPACE = "$BASE_FILE_PATH$FILE_NAME_SPACE"
	}


	@Test
	fun testCreate() {
		val handInSubmissionDTO = HandInSubmissionDTO(
			submissionResource.buildJwt(PERSON, TEAM, BASE_S3_URL, FULL_FILE_NAME, FILE_NAME, "image/png").sign(),
			"$BASE_S3_URL/$FULL_FILE_NAME"
		)
		assertDoesNotThrow {
			submissionResource.submit(handInSubmissionDTO)
		}
	}

	@Test
	fun testCreate_spaces() {
		val handInSubmissionDTO = HandInSubmissionDTO(
			submissionResource.buildJwt(PERSON, TEAM, BASE_S3_URL, FULL_FILE_NAME_SPACE, FILE_NAME_SPACE, "image/png").sign(),
			"$BASE_S3_URL/$FULL_FILE_NAME_SPACE"
		)
		assertDoesNotThrow {
			// used to throw java.net.URISyntaxException: Illegal character in path when using spaces
			submissionResource.submit(handInSubmissionDTO)
		}
	}
}
