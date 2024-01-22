package com.github.contestsubmission.backend.util.db

import org.hibernate.Hibernate

interface LazyFetchable {
	fun fetch() = toFetch().forEach(Hibernate::initialize)

	fun toFetch(): Collection<Any?>
}
