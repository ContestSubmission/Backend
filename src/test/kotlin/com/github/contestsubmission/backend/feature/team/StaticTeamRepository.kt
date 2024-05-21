package com.github.contestsubmission.backend.feature.team

import java.util.*

class StaticTeamRepository(vararg teams: Team) : TeamRepository() {
	val teamMap = teams.associateBy { it.id }

	override fun findByIdOrNull(id: UUID): Team? {
		return teamMap[id]
	}
}
