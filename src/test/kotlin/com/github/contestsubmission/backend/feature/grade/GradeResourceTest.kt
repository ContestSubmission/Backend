package com.github.contestsubmission.backend.feature.grade

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.feature.contest.ContestRepository
import com.github.contestsubmission.backend.feature.grade.dto.GradeTeamOverviewDTO
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import io.quarkus.test.junit.QuarkusTest
import jakarta.ws.rs.ForbiddenException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@QuarkusTest
class GradeResourceTest {
	val organizerPerson = Person(UUID.fromString("aca81730-3fad-420b-aef9-bcd33d2a7206"))
	val participantPerson = Person(UUID.fromString("b77f09f3-3b4d-4608-bafc-8c7a31a7b559"))
	val contestUUID = UUID.randomUUID()!!

	fun constructGradeResource(
		deadline: Instant,
		user: Person,
		publicGrading: Boolean = true
	) = GradeResource().apply {
		contestId = contestUUID
		userAuthenticationService = object : UserAuthenticationService() {
			override fun getUserOrNull() = user
		}
		contestRepository = object : ContestRepository() {
			override fun findByIdOrNull(id: UUID) = Contest(
				id = contestUUID,
				name = "My contest",
				organizer = organizerPerson,
				deadline = deadline,
				publicGrading = publicGrading
			)
		}
		gradeRepository = object : GradeRepository() {
			override fun getByContest(contestId: UUID, personId: UUID?): List<GradeTeamOverviewDTO> {
				return emptyList()
			}
		}
	}

	@Test
	fun testList_ongoing_organizer() {
		// works - organizer can always see submissions
		assertDoesNotThrow {
			constructGradeResource(Instant.now().plus(7, ChronoUnit.DAYS), organizerPerson)
				.list()
		}
	}

	@Test
	fun testList_ongoing_participant() {
		// fails - participant can't see submissions while contest is ongoing
		assertThrows<ForbiddenException> {
			constructGradeResource(Instant.now().plus(7, ChronoUnit.DAYS), participantPerson)
				.list()
		}
	}

	@Test
	fun testList_past_organizer() {
		// works - organizer can always see submissions
		assertDoesNotThrow {
			constructGradeResource(Instant.now().minus(7, ChronoUnit.DAYS), organizerPerson)
				.list()
		}
	}

	@Test
	fun testList_past_participant() {
		// works - participant can see submissions of done contest
		assertDoesNotThrow {
			constructGradeResource(Instant.now().minus(7, ChronoUnit.DAYS), participantPerson)
				.list()
		}
	}

	@Test
	fun testList_privateGrading_ongoing() {
		// fails - public grading disabled
		assertThrows<ForbiddenException> {
			constructGradeResource(Instant.now().plus(7, ChronoUnit.DAYS), participantPerson, publicGrading = false)
				.list()
		}
	}

	@Test
	fun testList_privateGrading_past() {
		// fails - public grading disabled
		assertThrows<ForbiddenException> {
			constructGradeResource(Instant.now().minus(7, ChronoUnit.DAYS), participantPerson, publicGrading = false)
				.list()
		}
	}
}
