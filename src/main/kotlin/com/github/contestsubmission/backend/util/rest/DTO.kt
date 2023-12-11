package com.github.contestsubmission.backend.util.rest

interface DTO<E> {
	fun toEntity(): E
}
