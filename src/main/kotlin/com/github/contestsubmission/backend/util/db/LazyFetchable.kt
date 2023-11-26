package com.github.contestsubmission.backend.util.db

import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny

interface LazyFetchable {
	/**
	 * Fetches all entities that are lazy loaded.
	 * This is useful for when you want to return the entity to the client.
	 * Use [Mutiny.fetch] to initialize the lazy loaded entities.
	 */
	fun fetch(): Uni<*>
}
