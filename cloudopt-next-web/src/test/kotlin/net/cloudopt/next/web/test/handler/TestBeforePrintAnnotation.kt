package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.annotation.Before

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@Before(invokeBy = [TestBeforePrintHandler::class])
annotation class TestBeforePrintAnnotation()