package com.github.contestsubmission.backend.feature.user

import com.github.contestsubmission.backend.util.db.CRUDRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class PersonRepository : CRUDRepository<Person, UUID>() {
	override val entityClass: Class<Person> = Person::class.java
}
