package com.github.contestsubmission.backend.util.db

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session

suspend fun <T> Mutiny.SessionFactory.transaction(block: Session.() -> Uni<T>): T? = this.withTransaction(block).awaitSuspending()

suspend fun <T> Mutiny.SessionFactory.persist(entity: T): T {
	this.transaction { persist(entity) }
	return entity
}

suspend fun <T, I> Mutiny.SessionFactory.find(entityClass: Class<T>, id: I): T? = this.transaction { find(entityClass, id) }

suspend fun <T : LazyFetchable, I> Mutiny.SessionFactory.findFullFetch(entityClass: Class<T>, id: I): T? = this.transaction {
	find(entityClass, id).onItem().ifNotNull().call { entity: T? -> entity?.fetch() }
}
