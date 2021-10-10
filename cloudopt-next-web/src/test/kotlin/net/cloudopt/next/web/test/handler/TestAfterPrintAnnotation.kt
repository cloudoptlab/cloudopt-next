package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.annotation.Before

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@Before(invokeBy = [TestAfterPrintHandler::class])
annotation class TestAfterPrintAnnotation()