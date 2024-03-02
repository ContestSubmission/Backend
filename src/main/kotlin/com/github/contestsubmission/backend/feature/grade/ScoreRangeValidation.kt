package com.github.contestsubmission.backend.feature.grade

import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ScoreRangeValidator::class])
@MustBeDocumented
annotation class ScoreRange(
	val message: String = "Score out of range",
	val groups: Array<KClass<*>> = [],
	val payload: Array<KClass<out Payload>> = []
)

@ApplicationScoped
class ScoreRangeValidator : ConstraintValidator<ScoreRange, Int> {
	override fun isValid(value: Int, context: ConstraintValidatorContext): Boolean {
		/*
		will be used once the value range is configurable
		if (context is HibernateConstraintValidatorContext) {
			val payload = context.unwrap(HibernateConstraintValidatorContext::class.java)
				.getConstraintValidatorPayload(Contest::class.java)
		}
		*/
		return value in 0..100
	}
}
