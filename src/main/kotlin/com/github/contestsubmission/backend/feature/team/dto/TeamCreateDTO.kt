package com.github.contestsubmission.backend.feature.team.dto

import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.util.rest.DTO
import jakarta.validation.constraints.NotBlank
import org.eclipse.microprofile.openapi.annotations.media.Schema

data class TeamCreateDTO(
	@field:NotBlank
	@field:Schema(defaultValue = "My Team")
	val name: String,
) : DTO<Team> {
	override fun toEntity() = Team(
		name = name
	)
}
