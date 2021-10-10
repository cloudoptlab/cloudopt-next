package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.annotation.Before

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
@Before(invokeBy = [TestAfterPrint2Handler::class])
annotation class TestAfterPrint2Annotation()