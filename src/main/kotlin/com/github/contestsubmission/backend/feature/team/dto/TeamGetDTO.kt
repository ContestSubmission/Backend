package com.github.contestsubmission.backend.feature.team.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.rest.FromEntityDTO
import java.util.*

data class TeamGetDTO(
	val id: UUID,
	val name: String,
	val owner: Person,
	@JsonIgnoreProperties("teams")
	val contest: Contest,
	val members: MutableSet<Person>,
	@JsonIgnoreProperties("team")
	val submissions: MutableSet<Submission>
) {
	companion object : FromEntityDTO<Team, TeamGetDTO> {
		override fun fromEntity(entity: Team): TeamGetDTO = entity.run {
			TeamGetDTO(
				id = id!!,
				name = name,
				owner = owner,
				contest = contest,
				members = members,
				submissions = submissions
			)
		}
	}
}
