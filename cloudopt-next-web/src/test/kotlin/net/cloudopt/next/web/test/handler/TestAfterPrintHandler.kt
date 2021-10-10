package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.RouteHandler

class TestAfterPrintHandler : RouteHandler {

    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val afterAnnotation = annotation as TestAfterPrintAnnotation
        println("Pass the first TestAfterPrintHandler")
        return true
    }
}