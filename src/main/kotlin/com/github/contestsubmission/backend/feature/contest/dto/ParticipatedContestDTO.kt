package com.github.contestsubmission.backend.feature.contest.dto

import java.util.UUID

data class ParticipatedContestDTO(
	val id: UUID,
	val name: String,
	val manager: Boolean
)
