package net.cloudopt.next.web

import net.cloudopt.next.web.annotation.Validator
import kotlin.reflect.full.createInstance

class ValidatorRouteHandler : RouteHandler {
    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val validatorAnnotation = annotation as Validator
        validatorAnnotation.value.forEach { validatorClass ->
            val v = validatorClass.createInstance()
            if (!v.validate(resource)) {
                v.error(resource)
                return false
            }
        }
        return true
    }
}