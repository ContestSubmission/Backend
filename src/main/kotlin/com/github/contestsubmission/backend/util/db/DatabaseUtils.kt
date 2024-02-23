package com.github.contestsubmission.backend.util.db

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple


fun <T : LazyFetchable, I> EntityManager.findFullFetch(entityClass: Class<T>, id: I): T? =
	find(entityClass, id)?.also { entity: T? -> entity?.fetch() }

fun <T> Tuple.getOrNull(alias: String, type: Class<T>): T? =
	if (elements.any { it.alias == alias }) {
		get(alias, type)
	} else {
		null
	}
