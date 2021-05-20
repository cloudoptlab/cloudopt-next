package net.cloudopt.next.web.annotation

import net.cloudopt.next.web.RouteHandler
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Before(
    val invokeBy: Array<KClass<out RouteHandler>> = []
)