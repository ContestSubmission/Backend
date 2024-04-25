package com.github.contestsubmission.backend.feature.grade

import com.github.contestsubmission.backend.feature.contest.ContestRepository
import com.github.contestsubmission.backend.feature.grade.dto.GradeCreateDTO
import com.github.contestsubmission.backend.feature.grade.dto.GradeTeamOverviewDTO
import com.github.contestsubmission.backend.feature.submission.SubmissionRepository
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import com.github.contestsubmission.backend.util.findById
import com.github.contestsubmission.backend.util.tryValidate
import getUser
import io.quarkus.security.Authenticated
import jakarta.inject.Inject
import jakarta.validation.ValidatorFactory
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.hibernate.validator.HibernateValidatorFactory
import java.util.*


@Path("/contest/{contestId}/grade")
class GradeResource {
	@PathParam("contestId")
	lateinit var contestId: UUID

	@Inject
	lateinit var gradeRepository: GradeRepository

	@Inject
	lateinit var contestRepository: ContestRepository

	@Inject
	lateinit var submissionRepository: SubmissionRepository

	@Inject
	lateinit var userAuthenticationService: UserAuthenticationService

	@Inject
	lateinit var validatorFactory: ValidatorFactory

	@POST
	@Path("{submissionId}")
	@Authenticated
	@APIResponse(responseCode = "201", description = "Grade created")
	fun grade(@PathParam("submissionId") submissionId: Long, gradeCreateDTO: GradeCreateDTO): Response {
		val caller = userAuthenticationService.getUser()
		val contest = contestRepository.findById(contestId)

		// will be replaced by RBAC in the future
		if (contest.organizer?.id?.equals(caller.id) != true && !contest.publicGrading) {
			throw ForbiddenException("Not the organizer of the contest and public grading is disabled")
		}

		if (!contest.hasEnded()) {
			throw BadRequestException("Contest is still in progress")
		}

		val validator = validatorFactory.unwrap(HibernateValidatorFactory::class.java)
			.usingContext()
			.constraintValidatorPayload(contest)
			.validator

		validator.tryValidate(gradeCreateDTO)

		if (!submissionRepository.isLatestSubmission(submissionId)) {
			throw BadRequestException("Outdated submission")
		}

		val grade = Grade(
			personId = caller.id!!,
			submissionId = submissionId,
			score = gradeCreateDTO.score,
			comment = gradeCreateDTO.comment
		)

		gradeRepository.merge(grade)

		return Response.status(Response.Status.CREATED).build()
	}

	@GET
	@Path("list")
	@Authenticated
	fun list(): List<GradeTeamOverviewDTO> {
		val caller = userAuthenticationService.getUser()
		val contest = contestRepository.findById(contestId)

		// will be replaced by RBAC in the future
		if (contest.organizer?.id?.equals(caller.id) != true && (!contest.publicGrading || !contest.hasEnded())) {
			throw ForbiddenException("Not the organizer of the contest and public grading is disabled or contest is in progress")
		}

		return gradeRepository.getByContest(contestId, caller.id)
	}
}
