package com.github.contestsubmission.backend.feature.grade.dto

import com.github.contestsubmission.backend.feature.grade.ScoreRange
import org.eclipse.microprofile.openapi.annotations.media.Schema

data class GradeCreateDTO(
	@field:ScoreRange
	@field:Schema(example = "100")
	val score: Int,
	@field:Schema(example = "Good job!")
	val comment: String?
)
