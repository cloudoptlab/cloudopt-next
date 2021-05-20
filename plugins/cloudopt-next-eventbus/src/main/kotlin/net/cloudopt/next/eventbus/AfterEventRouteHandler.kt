package net.cloudopt.next.eventbus

import io.vertx.core.json.JsonObject
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.RouteHandler

/**
 * When the @AfterEvent annotation is declared, the data in the context object is automatically sent to the
 * specified event group.
 */
class AfterEventRouteHandler : RouteHandler {

    override suspend fun handle(annotation: Annotation, resource: Resource): Boolean {
        val afterAnnotation = annotation as AfterEvent
        afterAnnotation.value.forEach { address ->
            EventBusManager.send(address = address, message = JsonObject(resource.context.data()))
        }
        return true
    }
}