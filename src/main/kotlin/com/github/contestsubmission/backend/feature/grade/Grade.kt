package com.github.contestsubmission.backend.feature.grade

import com.github.contestsubmission.backend.feature.submission.Submission
import com.github.contestsubmission.backend.feature.user.Person
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
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
	@ManyToOne
	var submission: Submission? = null,
	@MapsId("personId")
	@ManyToOne
	var person: Person? = null
) {
	constructor() : this(0, UUID.randomUUID(), 0, null)
}
