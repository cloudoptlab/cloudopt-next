package net.cloudopt.next.web

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class After(
    val invokeBy: Array<KClass<out Invoker>> = []
)