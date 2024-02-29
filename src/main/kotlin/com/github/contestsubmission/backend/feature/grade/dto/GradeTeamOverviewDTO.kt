package com.github.contestsubmission.backend.feature.grade.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.contestsubmission.backend.feature.grade.Grade
import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.team.dto.EnumeratedTeamDTO

data class GradeTeamOverviewDTO(
	@JsonIgnoreProperties("team")
	val submission: Submission?,
	val team: EnumeratedTeamDTO,
	val scoreCount: Long,
	val score: Long,
	val personalGrade: Grade?
)
