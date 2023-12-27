package com.github.contestsubmission.backend.feature.contest.dto

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.util.rest.ToEntityDTO
import jakarta.validation.constraints.*
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.LocalDateTime

data class ContestCreateDTO(
	@field:NotBlank
	@field:Schema(defaultValue = "My Contest")
	val name: String,
	val description: String? = null,
	@field:Future
	@field:Schema(example = "2025-05-10T00:00:00")
	val deadline: LocalDateTime,
	@field:Min(1)
	@field:Max(50)
	@field:Schema(example = "5")
	val maxTeamSize: Int,
	val public: Boolean = false
) : ToEntityDTO<Contest> {
	override fun toEntity() = Contest(
		name = name,
		description = description,
		deadline = deadline,
		maxTeamSize = maxTeamSize,
		public = public
	)
}
