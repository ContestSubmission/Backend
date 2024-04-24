package com.github.contestsubmission.backend.feature.grade

import com.github.contestsubmission.backend.feature.contest.ContestRepository
import com.github.contestsubmission.backend.feature.grade.dto.GradeCreateDTO
import com.github.contestsubmission.backend.feature.grade.dto.GradeTeamOverviewDTO
import com.github.contestsubmission.backend.feature.submission.SubmissionRepository
import com.github.contestsubmission.backend.feature.user.UserAuthenticationService
import com.github.contestsubmission.backend.util.tryValidate
import io.quarkus.security.Authenticated
import io.quarkus.security.UnauthorizedException
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
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")
		val contest = contestRepository.findById(contestId) ?: throw NotFoundException("Contest not found")

		// will be replaced by RBAC in the future
		if (contest.organizer?.id?.equals(caller.id) != true && !contest.publicGrading) {
			return Response.status(Response.Status.FORBIDDEN).entity("Not the organizer of the contest and public grading is disabled").build()
		}

		if (!contest.hasEnded()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Contest is still in progress!").build()
		}

		val validator = validatorFactory.unwrap(HibernateValidatorFactory::class.java)
			.usingContext()
			.constraintValidatorPayload(contest)
			.validator

		validator.tryValidate(gradeCreateDTO)

		if (!submissionRepository.isLatestSubmission(submissionId)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Outdated submission").build()
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
		val caller = userAuthenticationService.getUser() ?: throw UnauthorizedException("Not logged in")
		val contest = contestRepository.findById(contestId) ?: throw NotFoundException("Contest not found")

		// will be replaced by RBAC in the future
		if (contest.organizer?.id?.equals(caller.id) != true && !contest.publicGrading) {
			throw ForbiddenException("Not the organizer of the contest and public grading is disabled")
		}

		return gradeRepository.getByContest(contestId, caller.id)
	}
}
