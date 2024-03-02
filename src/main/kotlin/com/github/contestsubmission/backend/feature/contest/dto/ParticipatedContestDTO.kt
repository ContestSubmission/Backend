package com.github.contestsubmission.backend.feature.contest.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.github.contestsubmission.backend.feature.team.Team
import java.time.LocalDateTime
import java.util.*

data class ParticipatedContestDTO(
	override val id: UUID,
	override val name: String,
	override val deadline: LocalDateTime,
	val manager: Boolean,
	@JsonIgnoreProperties("contest", "members", "submissions")
	@JsonInclude(Include.NON_NULL)
	val team: Team? = null
) : ContestDTO {
	// fuck you JPA
	constructor(id: UUID, name: String, deadline: LocalDateTime, manager: Boolean) : this(id, name, deadline, manager, null)
}
