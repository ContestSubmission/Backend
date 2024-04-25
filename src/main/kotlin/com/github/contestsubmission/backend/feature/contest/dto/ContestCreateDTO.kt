package com.github.contestsubmission.backend.feature.contest.dto

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.util.rest.ToEntityDTO
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.Instant

data class ContestCreateDTO(
	@field:NotBlank
	@field:Schema(example = "My Contest")
	val name: String,
	val description: String?,
	@field:Future
	@field:Schema(example = "2025-05-10T10:00:00.000Z")
	val deadline: Instant,
	@field:Min(1)
	@field:Max(50)
	@field:Schema(example = "5")
	val maxTeamSize: Int,
	val publicAccessible: Boolean,
	val publicGrading: Boolean
) : ToEntityDTO<Contest> {
	override fun toEntity() = Contest(
		name = name,
		description = description,
		deadline = deadline,
		maxTeamSize = maxTeamSize,
		publicAccessible = publicAccessible,
		publicGrading = publicGrading
	)
}
