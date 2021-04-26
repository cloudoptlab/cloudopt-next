package net.cloudopt.next.web.test.controller

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.route.API
import net.cloudopt.next.web.route.GET
import net.cloudopt.next.web.test.interceptor.TestInterceptor
import net.cloudopt.next.web.test.validator.TestValidator

@API("/interceptor",interceptor = [TestInterceptor::class])
class TestInterceptorController:Resource() {

    @GET
    fun testGet(){
        renderText("success")
    }

    @GET("/validator",valid = [TestValidator::class])
    fun testValidator(){
        renderText("success")
    }

}