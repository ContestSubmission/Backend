package com.github.contestsubmission.backend.util.rest

import com.github.contestsubmission.backend.util.exception.DTOMappingException
import kotlin.reflect.KMutableProperty1

interface ToEntityDTO<E> {
	fun toEntity(): E
}

interface FromEntityDTO<E, DTO> {
	fun fromEntity(entity: E): DTO
}

fun <E, R> E.mapDTO(mapper: E.() -> R): R = try {
	this.run(mapper)
} catch (e: Exception) {
	throw DTOMappingException(e)
}

interface ApplyToEntityDTO<E> {
	fun applyToEntity(entity: E)
}

fun <T, E> E.applyIfSet(property: KMutableProperty1<E, T>, value: T?) {
	if (value != null) {
		property.set(this, value)
	}
}
