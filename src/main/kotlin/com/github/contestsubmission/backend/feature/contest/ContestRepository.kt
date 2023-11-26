package com.github.contestsubmission.backend.feature.contest

import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class ContestRepository : CRUDRepository<Contest, UUID>() {
	override val entityClass: Class<Contest> = Contest::class.java
}
