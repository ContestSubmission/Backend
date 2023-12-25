package com.github.contestsubmission.backend.util.db

import jakarta.inject.Inject
import jakarta.validation.Valid
import org.hibernate.reactive.mutiny.Mutiny

/**
 * base class for CRUD repositories abstracting away some of the boilerplate code
  */
abstract class CRUDRepository<T, I> {
	@Inject
	lateinit var sessionFactory: Mutiny.SessionFactory

	abstract val entityClass: Class<T>

	open suspend fun persist(@Valid entity: T): T = sessionFactory.persist(entity)

	open suspend fun findById(id: I): T? = sessionFactory.find(entityClass, id)
}

/**
 * Manually fetches an entity plus the lazy properties using [LazyFetchable.fetch]
 */
suspend fun <T : LazyFetchable, I> CRUDRepository<T, I>.findByIdFullFetch(id: I): T? =
	sessionFactory.findFullFetch(entityClass, id)
