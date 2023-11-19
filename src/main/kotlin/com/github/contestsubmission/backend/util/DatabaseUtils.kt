package com.github.contestsubmission.backend.util

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
