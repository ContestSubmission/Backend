package com.github.contestsubmission.backend.feature.contest.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import java.time.LocalDateTime
import java.util.*

data class PersonalContestDTO(
	val id: UUID,
	val name: String,
	val organizer: Person,
	val description: String?,
	val publicGrading: Boolean,
	val deadline: LocalDateTime,
	val maxTeamSize: Int,
	@JsonIgnoreProperties("contest")
	val team: Team?
) {
	@JsonIgnoreProperties("team")
	var submissions: List<Submission>? = null
}
