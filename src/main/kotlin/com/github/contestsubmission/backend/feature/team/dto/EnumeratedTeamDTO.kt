package com.github.contestsubmission.backend.feature.team.dto

import com.github.contestsubmission.backend.feature.user.Person
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.*

data class EnumeratedTeamDTO(
	val id: UUID,
	val name: String,
	val owner: Person,
	@field:Schema(defaultValue = "1")
	val memberCount: Int,
	val submissionCount: Int
) {
	companion object {
		fun toJPAQuery(teamColumn: String): String
			= """NEW ${EnumeratedTeamDTO::class.qualifiedName}(
				${teamColumn}.id,
				${teamColumn}.name,
				${teamColumn}.owner,
				size(${teamColumn}.members),
				size(${teamColumn}.submissions)
			)""".trimIndent()
	}
}
