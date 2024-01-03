package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.LazyFetchable
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Contest(
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	var id: UUID? = null,
	@Column(nullable = false)
	var name: String = "",
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	var organizer: Person = Person(),
	var description: String? = null,
	var public: Boolean = false,
	var deadline: LocalDateTime = LocalDateTime.now().plusDays(7),
	var maxTeamSize: Int = 1
) : LazyFetchable {
	override fun toFetch() = listOf(organizer)
}
