package com.github.contestsubmission.backend.util.db

import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import jakarta.validation.Valid

/**
 * base class for CRUD repositories abstracting away some of the boilerplate code
  */
abstract class CRUDRepository<T, I> {
	@Inject
	lateinit var entityManager: EntityManager

	abstract val entityClass: Class<T>

	@Transactional
	open fun persist(@Valid entity: T): T {
		entityManager.persist(entity)
		return entity
	}

	open fun findById(id: I): T? = entityManager.find(entityClass, id)
}

/**
 * Manually fetches an entity plus the lazy properties using [LazyFetchable.fetch]
 */
fun <T : LazyFetchable, I> CRUDRepository<T, I>.findByIdFullFetch(id: I): T? =
	entityManager.findFullFetch(entityClass, id)
