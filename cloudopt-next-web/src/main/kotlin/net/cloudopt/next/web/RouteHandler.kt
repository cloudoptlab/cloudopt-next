package net.cloudopt.next.web

/**
 * Define an execution process.
 */
interface RouteHandler {

    /**
     * Define the content to be intercepted, if false it will not be executed further.
     * @param annotation An annotation class that declares an annotation
     * @param resource Resource
     * @return Boolean
     */
    suspend fun handle(annotation: Annotation, resource: Resource): Boolean

}