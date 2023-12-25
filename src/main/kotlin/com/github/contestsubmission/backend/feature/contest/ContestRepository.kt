package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.util.db.CRUDRepository
import com.github.contestsubmission.backend.util.db.transaction
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class ContestRepository : CRUDRepository<Contest, UUID>() {
	override val entityClass: Class<Contest> = Contest::class.java

	suspend fun search(term: String): MutableList<Contest>? = sessionFactory.transaction {
		it.createQuery("""
			SELECT c FROM Contest c WHERE
				(lower(c.name) LIKE lower(:term) OR lower(c.description) LIKE lower(:term))
				AND c.public
		""".trimIndent(), Contest::class.java)
			.setParameter("term", "%$term%")
			.resultList
	}
}
