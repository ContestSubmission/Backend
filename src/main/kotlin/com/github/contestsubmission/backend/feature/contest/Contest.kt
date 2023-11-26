package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.LazyFetchable
import io.smallrye.mutiny.Uni
import jakarta.persistence.*
import jakarta.validation.constraints.Future
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.validator.constraints.Range
import java.time.LocalDateTime
import java.util.*

@Entity
class Contest(
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	var id: UUID? = null,
	@Column(nullable = false)
	var name: String,
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(nullable = false)
	var organizer: Person,
	var description: String? = null,
	@Future
	@field:Future
	var deadline: LocalDateTime,
	@Range(min = 1, max = 50)
	@field:Range(min = 1, max = 50)
	var maxTeamSize: Int
) : LazyFetchable {
	constructor() : this(
		name = "",
		organizer = Person(),
		deadline = LocalDateTime.now(),
		maxTeamSize = 1
	)

	override fun fetch(): Uni<Person> = Mutiny.fetch(organizer)
}
