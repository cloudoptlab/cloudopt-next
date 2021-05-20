package net.cloudopt.next.grpc

import io.grpc.ServerInterceptor
import io.grpc.ServerInterceptors
import io.vertx.grpc.VertxServerBuilder
import io.vertx.kotlin.coroutines.CoroutineVerticle
import net.cloudopt.next.core.Worker
import net.cloudopt.next.logging.Logger
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

/**
 * A Verticle which run its start and stop methods in coroutine. The vertx server
 * is automatically built at startup and the service classes of the scanned grpc
 * are automatically registered.
 * @property logger Logger
 */
class GrpcVerticle : CoroutineVerticle() {

    private val logger = Logger.getLogger(this::class)

    override suspend fun start() {
        val builder =
            VertxServerBuilder.forPort(Worker.vertx, (GrpcManager.config["port"] ?: 9090).toString().toInt())
        GrpcManager.grpcServiceList.forEach { clazz ->
            val annotation = clazz.findAnnotation<GrpcService>()
            if (annotation?.interceptors?.isNotEmpty() == true) {
                var interceptorInstances: Array<ServerInterceptor> = arrayOf()
                annotation.interceptors.forEach { interceptorClazz ->
                    interceptorInstances = interceptorInstances.plus(interceptorClazz.createInstance())
                }

                builder.addService(ServerInterceptors.intercept(clazz.createInstance(), *interceptorInstances))
            } else {
                builder.addService(clazz.createInstance())
            }
            logger.info("[GRPC] Registration Services: ${clazz.jvmName}")
        }
        if ((GrpcManager.config["ssl"] ?: true).toString().toBoolean()) {
            builder.useSsl { options ->
                GrpcManager.optionsHandler.handle(options)
            }
        }
        GrpcManager.grpcServer = builder.build()
        GrpcManager.grpcServer.start { it ->
            if (it.succeeded()) {
                logger.info(
                    "[GRPC] Grpc server started successfully and is listening to port: ${
                        (GrpcManager.config["port"] ?: "9090").toString().toInt()
                    }."
                )
            } else {
                logger.info("[GRPC] Grpc server failed to start!")
            }

        }
    }

    override suspend fun stop() {
        GrpcManager.grpcServer.shutdown { it ->
            if (it.succeeded()) {
                logger.info("Grpc server stopped successfully.")
            } else {
                logger.info("Grpc server failed to stop!")
            }
        }
    }

}