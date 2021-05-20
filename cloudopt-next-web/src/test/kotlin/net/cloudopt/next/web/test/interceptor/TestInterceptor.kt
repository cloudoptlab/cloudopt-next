package net.cloudopt.next.web.test.interceptor

import net.cloudopt.next.web.Interceptor
import net.cloudopt.next.web.Resource

class TestInterceptor : Interceptor {


    override suspend fun intercept(resource: Resource): Boolean {

        resource.setCookie("before", "interceptor")

        return true

    }


    override suspend fun response(resource: Resource): Resource {
        return resource
    }


}