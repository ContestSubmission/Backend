package com.github.contestsubmission.backend.feature.submission

import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class SubmissionRepository : CRUDRepository<Submission, UUID>(Submission::class) {
	override val entityName = Submission.ENTITY_NAME

	fun isLatestSubmission(submissionId: Long): Boolean =
		entityManager.createQuery(
			"""
				SELECT COUNT(s) FROM Submission s
				WHERE s.id = :id
					AND s.handedInAt = (SELECT MAX(s2.handedInAt) FROM Submission s2 WHERE s2.team.id = s.team.id)
			""".trimIndent(),
			Long::class.java
		).setParameter("id", submissionId).singleResult == 1L
}
