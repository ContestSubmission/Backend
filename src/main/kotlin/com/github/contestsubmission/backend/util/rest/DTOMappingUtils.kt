package com.github.contestsubmission.backend.util.rest

import com.github.contestsubmission.backend.util.exception.DTOMappingException

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
