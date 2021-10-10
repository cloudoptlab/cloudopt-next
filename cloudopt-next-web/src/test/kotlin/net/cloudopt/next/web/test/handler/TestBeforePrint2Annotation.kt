package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.annotation.Before

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@Before(invokeBy = [TestBeforePrint2Handler::class])
annotation class TestBeforePrint2Annotation()