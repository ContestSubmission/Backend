package com.github.contestsubmission.backend.feature.team

import com.github.contestsubmission.backend.feature.contest.Contest
import com.github.contestsubmission.backend.feature.user.Person
import com.github.contestsubmission.backend.util.db.LazyFetchable
import io.smallrye.mutiny.Uni
import jakarta.persistence.*
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*

@Entity
class Team(
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	var id: UUID? = null,
	@Column(nullable = false)
	var name: String = "",
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	var owner: Person = Person(),
	// despite the fact that lazy would be smarter here, it doesn't work
	// that is because QUARKUS FUCKING SUCKS YET AGAIN
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	var contest: Contest = Contest(),
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "team_member",
		joinColumns = [JoinColumn(name = "team_id")],
		inverseJoinColumns = [JoinColumn(name = "person_id")]
	)
	var members: MutableSet<Person> = mutableSetOf()
) : LazyFetchable {
	override fun fetch(): Uni<Team> = Uni.combine().all().unis(
		Mutiny.fetch(owner),
		Mutiny.fetch(contest),
		Mutiny.fetch(members)
	).asTuple().onItem().transform { this }
}
