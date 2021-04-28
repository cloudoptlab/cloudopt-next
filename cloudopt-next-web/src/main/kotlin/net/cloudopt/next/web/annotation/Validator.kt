package net.cloudopt.next.web.annotation

import net.cloudopt.next.web.ValidatorRouteHandler
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@Before(invokeBy = [ValidatorRouteHandler::class])
annotation class Validator(val value: Array<KClass<out net.cloudopt.next.web.Validator>> = [])
