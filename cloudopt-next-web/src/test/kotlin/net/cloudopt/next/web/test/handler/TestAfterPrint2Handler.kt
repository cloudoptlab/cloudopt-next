package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.RouteHandler

class TestAfterPrint2Handler : RouteHandler {

    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val afterAnnotation = annotation as TestAfterPrint2Annotation
        println("Pass the first TestAfterPrint2Handler")
        return true
    }
}