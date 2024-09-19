package com.github.contestsubmission.backend.feature.contest.dto

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.util.rest.ApplyToEntityDTO
import com.github.contestsubmission.backend.util.rest.applyIfSet
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.Instant

data class ContestUpdateDTO(
	@field:Schema(example = "My Contest")
	val name: String?,
	val description: String?,
	@field:Future
	@field:Schema(example = "2025-05-10T10:00:00.000Z")
	val deadline: Instant?,
	@field:Min(1)
	@field:Max(50)
	@field:Schema(example = "5")
	val maxTeamSize: Int?,
	val defaultViewMode: String?,
	val publicAccessible: Boolean?,
	val publicGrading: Boolean?
) : ApplyToEntityDTO<Contest> {
	override fun applyToEntity(entity: Contest) {
		entity.applyIfSet(Contest::name, name)
		entity.applyIfSet(Contest::description, description)
		entity.applyIfSet(Contest::deadline, deadline)
		entity.applyIfSet(Contest::maxTeamSize, maxTeamSize)
		entity.applyIfSet(Contest::defaultViewMode, defaultViewMode)
		entity.applyIfSet(Contest::publicAccessible, publicAccessible)
		entity.applyIfSet(Contest::publicGrading, publicGrading)
	}
}
