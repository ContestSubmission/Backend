package com.github.contestsubmission.backend.feature.contest.dto

import java.time.Instant
import java.util.*

interface ContestDTO {
	val id: UUID
	val name: String
	val deadline: Instant
}
