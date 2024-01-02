package com.github.contestsubmission.backend.feature.team.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.util.rest.ToEntityDTO
import jakarta.validation.constraints.NotBlank
import org.eclipse.microprofile.openapi.annotations.media.Schema

// @JsonCreator needed because jackson is a piece of garbage
// literally can't deserialize a data class with only one field
// like dude what the fuck?
data class TeamCreateDTO @JsonCreator constructor(
	@field:NotBlank
	@field:Schema(defaultValue = "My Team")
	val name: String
) : ToEntityDTO<Team> {
	override fun toEntity() = Team(
		name = name
	)
}
