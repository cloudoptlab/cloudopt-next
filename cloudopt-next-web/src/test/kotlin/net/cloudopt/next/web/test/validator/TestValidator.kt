package net.cloudopt.next.web.test.validator

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Validator

class TestValidator : Validator {
    override suspend fun validate(resource: Resource): Boolean {
        println("via ${this.javaClass.simpleName}")
        return true
    }

    override suspend fun error(resource: Resource) {
        resource.response.statusCode = 400
        resource.renderText("error")
    }
}