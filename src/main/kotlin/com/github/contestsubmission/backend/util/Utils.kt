package com.github.contestsubmission.backend.util

import io.smallrye.jwt.build.JwtClaimsBuilder
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import java.time.Instant
import java.util.*
import kotlin.time.Duration
import kotlin.time.toJavaDuration

fun String.toUUID(): UUID? = UUID.fromString(this)

/**
 * NOT a drop-in replacement for [JwtClaimsBuilder.expiresIn] with a java duration!
 * Instead, this sets `expiredAt` to `Instant.now() + duration`
 */
fun JwtClaimsBuilder.expiresIn(duration: Duration): JwtClaimsBuilder = this.expiresAt(Instant.now() + duration.toJavaDuration())

/**
 * Validates the given entity and throws a [ConstraintViolationException] if it is not valid
 */
fun <T> Validator.tryValidate(entity: T) {
	val violations = this.validate(entity)
	if (violations.isNotEmpty()) {
		throw ConstraintViolationException(violations)
	}
}
