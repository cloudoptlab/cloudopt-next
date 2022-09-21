package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.handler.Handler

class TestGlobalHandler:Handler {
    override suspend fun preHandle(resource: Resource): Boolean {
        println("preHandle")
        resource.renderText("end")
        return false
    }

    override suspend fun postHandle(resource: Resource): Boolean {
        println("postHandle")
        return true
    }

    override suspend fun afterRender(resource: Resource, bodyString: String): Boolean {
        println("afterRender")
        return true
    }

    override suspend fun afterCompletion(resource: Resource): Boolean {
        println("afterCompletion")
        return true
    }
}