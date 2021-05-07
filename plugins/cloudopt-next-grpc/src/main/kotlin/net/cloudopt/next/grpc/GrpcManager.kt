package net.cloudopt.next.grpc

import io.grpc.BindableService
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerOptions
import io.vertx.grpc.VertxServer
import net.cloudopt.next.core.ConfigManager
import kotlin.reflect.KClass

/**
 * Used to store some static objects for grpc.
 */
object GrpcManager {
    @JvmStatic
    val config = ConfigManager.init("grpc")

    @JvmStatic
    lateinit var grpcServer: VertxServer

    @JvmStatic
    val grpcServiceList = mutableListOf<KClass<out BindableService>>()
    lateinit var optionsHandler: Handler<HttpServerOptions>
}