package net.cloudopt.next.web.test.controller

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.annotation.API
import net.cloudopt.next.web.annotation.GET
import net.cloudopt.next.web.annotation.Validator
import net.cloudopt.next.web.test.interceptor.TestInterceptor
import net.cloudopt.next.web.test.validator.TestThrowValidator
import net.cloudopt.next.web.test.validator.TestValidator
import net.cloudopt.next.web.test.validator.TestValidator2

@API("/interceptor", interceptor = [TestInterceptor::class])
class InterceptorController : Resource() {

    @GET
    suspend fun testGet() {
        renderText("success")
    }

    @GET("/validator")
    @Validator([TestValidator::class, TestValidator2::class])
    suspend fun testValidator() {
        renderText("success")
    }

    @GET("/throw")
    @Validator([TestThrowValidator::class])
    suspend fun testThrow() {
        renderText("success")
    }

}