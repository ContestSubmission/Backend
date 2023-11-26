package com.github.contestsubmission.backend.util.db

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.function.Function

suspend fun <T> Mutiny.SessionFactory.transaction(block: Function<Session, Uni<T>>): T? {
	val func: Function<Session, Uni<T>> = Function { session ->
		return@Function block.apply(session)
	}
	return this.withTransaction(func).awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.persist(entity: T): T {
	this.transaction { it.persist(entity) }
	return entity
}

suspend fun <T, I> Mutiny.SessionFactory.find(entityClass: Class<T>, id: I): T? {
	return this.transaction { it.find(entityClass, id) }
}

suspend fun <T : LazyFetchable, I> Mutiny.SessionFactory.findFullFetch(entityClass: Class<T>, id: I): T? {
	return this.transaction {
		it.find(entityClass, id).onItem().ifNotNull().call { entity: T? -> entity?.fetch() }
	}
}
