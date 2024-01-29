package com.github.contestsubmission.backend.feature.contest.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import java.time.LocalDateTime

data class PersonalContestDTO(
	val name: String,
	val organizer: Person,
	val description: String,
	val deadline: LocalDateTime,
	val maxTeamSize: Int,
	@JsonIgnoreProperties("contest")
	val team: Team?
)
