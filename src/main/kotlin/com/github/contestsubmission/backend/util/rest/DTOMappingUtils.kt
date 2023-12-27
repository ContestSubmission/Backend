package com.github.contestsubmission.backend.util.rest

import com.github.contestsubmission.backend.util.exception.DTOMappingException

interface ToEntityDTO<E> {
	fun toEntity(): E
}

interface FromEntityDTO<E, SELF : FromEntityDTO<E, SELF>> {
	fun fromEntity(entity: E): SELF
}

fun <E, R> E.mapDTO(mapper: E.() -> R): R = try {
	this.run(mapper)
} catch (e: Exception) {
	throw DTOMappingException(e)
}

interface DTO<E, SELF : DTO<E, SELF>> : ToEntityDTO<E>, FromEntityDTO<E, SELF>
