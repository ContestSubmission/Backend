package com.github.contestsubmission.backend.feature.contest.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import java.time.Instant
import java.util.*

data class PersonalContestDTO(
	override val id: UUID,
	override val name: String,
	val organizer: Person,
	val description: String?,
	val publicGrading: Boolean,
	override val deadline: Instant,
	val maxTeamSize: Int,
	val defaultViewMode: String,
	@JsonIgnoreProperties("contest")
	val team: Team?
) : ContestDTO {
	@JsonIgnoreProperties("team")
	var submissions: List<Submission>? = null
}
