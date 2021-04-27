package net.cloudopt.next.web

import javax.validation.ConstraintValidator
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Before(
    val invokeBy: Array<KClass<out Invoker>> = []
)