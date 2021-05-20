package net.cloudopt.next.web.test.validator

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Validator

class TestValidator2 : Validator {
    override suspend fun validate(resource: Resource): Boolean {
        println("via ${this.javaClass.simpleName}")
        return resource.getParam("name")?.isNotBlank() ?: false
    }

    override suspend fun error(resource: Resource) {
        resource.response.statusCode = 400
        resource.renderText("error")
    }
}