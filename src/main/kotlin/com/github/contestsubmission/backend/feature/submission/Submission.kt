package com.github.contestsubmission.backend.feature.submission

import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import jakarta.persistence.*
import java.time.Instant

@Entity
class Submission {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	var id: Long? = null

	var url: String? = null
	var fileName: String? = null
	@ManyToOne
	var uploadedBy: Person? = null
	@ManyToOne
	var team: Team? = null
	var handedInAt: Instant = Instant.now()

	override fun toString(): String {
		return "Submission(id=$id, url=$url, fileName=$fileName, uploadedBy=$uploadedBy, team=$team, handedInAt=$handedInAt)"
	}
}
