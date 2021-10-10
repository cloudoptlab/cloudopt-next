package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.RouteHandler

class TestBeforePrintHandler : RouteHandler {

    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val beforeAnnotation = annotation as TestBeforePrintAnnotation
        println("Pass the first TestBeforePrintHandler")
        return true
    }
}