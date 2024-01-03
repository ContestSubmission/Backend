package com.github.contestsubmission.backend.util.db

import jakarta.persistence.EntityManager


fun <T : LazyFetchable, I> EntityManager.findFullFetch(entityClass: Class<T>, id: I): T? =
	find(entityClass, id)?.also { entity: T? -> entity?.fetch() }
