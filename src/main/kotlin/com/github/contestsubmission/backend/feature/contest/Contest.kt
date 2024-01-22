package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.contest.dto.ContestDTO
import com.github.contestsubmission.backend.feature.team.Team
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.LazyFetchable
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

val DEFAULT_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

@Entity
class Contest(
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	override var id: UUID = DEFAULT_UUID,
	@Column(nullable = false)
	override var name: String = "",
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	var organizer: Person? = Person(),
	var description: String? = null,
	var public: Boolean = false,
	var deadline: LocalDateTime = LocalDateTime.now().plusDays(7),
	var maxTeamSize: Int = 1,
	@OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
	var teams: MutableList<Team>? = mutableListOf()
) : ContestDTO, LazyFetchable {
	override fun toFetch() = listOf(organizer, teams)
}
