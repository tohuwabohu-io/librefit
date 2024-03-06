package io.tohuwabohu.crud.util

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

/**
 * Custom annotation to check if a number matches a given set
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ OneOfValidator::class ])
annotation class OneOfFloat(
    val message: String = "...provide your default message here...",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = [],
    /** The array of allowed values.  */
    vararg val value: Float
)

class OneOfValidator : ConstraintValidator<OneOfFloat, Float> {
    private lateinit var allowedValues: Set<Float>

    override fun initialize(constraintAnnotation: OneOfFloat) {
        allowedValues = constraintAnnotation.value.toSet()
    }

    override fun isValid(p0: Float?, p1: ConstraintValidatorContext?): Boolean {
        return allowedValues.contains(p0)
    }
}