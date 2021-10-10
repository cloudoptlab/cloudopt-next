package net.cloudopt.next.web.test.handler

import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.RouteHandler

class TestBeforePrint2Handler : RouteHandler {

    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val beforeAnnotation = annotation as TestBeforePrint2Annotation
        println("Pass the first TestBeforePrint2Handler")
        return true
    }
}