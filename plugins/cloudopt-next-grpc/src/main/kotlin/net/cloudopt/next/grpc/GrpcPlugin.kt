package net.cloudopt.next.grpc

import io.grpc.BindableService
import net.cloudopt.next.core.Classer
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.core.Plugin
import net.cloudopt.next.core.Worker
import kotlin.reflect.KClass

/**
 * Grpc's plugin class, which automatically scans for classes containing the
 * @GrpcService annotation at startup, then saves them to
 * GrpcManager.grpcServiceList and deploys the GrpcVerticle.
 */
class GrpcPlugin : Plugin {

    override fun start(): Boolean {
        Classer.scanPackageByAnnotation(NextServer.packageName, true, GrpcService::class)
            .forEach { clazz ->
                GrpcManager.grpcServiceList.add(clazz as KClass<out BindableService>)
            }
        Worker.deploy("net.cloudopt.next.grpc.GrpcVerticle",workerPoolName = "net.cloudopt.next.grpc")
        return true
    }


    override fun stop(): Boolean {
        Worker.undeploy("net.cloudopt.next.grpc.GrpcVerticle")
        return true
    }

}