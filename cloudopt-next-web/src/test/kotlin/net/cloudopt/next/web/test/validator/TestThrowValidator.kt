package net.cloudopt.next.web.test.validator

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Validator
import java.lang.RuntimeException

class TestThrowValidator : Validator {
    override suspend fun validate(resource: Resource): Boolean {
        println("via ${this.javaClass.simpleName}")
        throw RuntimeException()
        return true
    }

    override suspend fun error(resource: Resource) {
        resource.response.statusCode = 400
        resource.renderText("error")
    }
}