package com.github.contestsubmission.backend.feature.grade

import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.user.Person
import jakarta.persistence.*
import java.util.*

@Entity
@IdClass(GradeId::class)
class Grade(
	@Id
	@Column(name = "submission_id")
	var submissionId: Long,
	@Id
	@Column(name = "person_id")
	var personId: UUID,
	var score: Int,
	var comment: String?,
	@MapsId("submissionId")
	@JoinColumn(name = "submission_id")
	@ManyToOne
	var submission: Submission? = null,
	@MapsId("personId")
	@JoinColumn(name = "person_id")
	@ManyToOne
	var person: Person? = null
) {
	constructor() : this(0, UUID.randomUUID(), 0, null)

	override fun toString(): String {
		return "Grade(submissionId=$submissionId, personId=$personId, score=$score, comment=$comment, submission=$submission, person=$person)"
	}

	companion object {
		const val ENTITY_NAME = "Grade"
	}
}
