package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class ContestRepository : CRUDRepository<Contest, UUID>() {
	override val entityClass: Class<Contest> = Contest::class.java

	fun search(term: String): MutableList<Contest>? =
		entityManager.createQuery("""
			SELECT c FROM Contest c WHERE
				(lower(c.name) LIKE lower(:term) OR lower(c.description) LIKE lower(:term))
				AND c.public
		""".trimIndent(), Contest::class.java)
			.setParameter("term", "%$term%")
			.resultList
}
