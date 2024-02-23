package com.github.contestsubmission.backend.feature.grade

import java.io.Serializable
import java.util.*


class GradeId(private var submissionId: Long, private var personId: UUID) : Serializable {
	// fuck you hibernate
	@Suppress("unused")
	constructor() : this(0, UUID.randomUUID())

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as GradeId

		if (submissionId != other.submissionId) return false
		if (personId != other.personId) return false

		return true
	}

	override fun hashCode(): Int {
		var result = submissionId.hashCode()
		result = 31 * result + personId.hashCode()
		return result
	}
}
