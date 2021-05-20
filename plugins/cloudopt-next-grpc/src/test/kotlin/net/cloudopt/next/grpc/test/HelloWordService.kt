package net.cloudopt.next.grpc.test

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.vertx.core.Future
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.grpc.GrpcService
import net.cloudopt.next.grpc.test.example.HelloReply
import net.cloudopt.next.grpc.test.example.HelloRequest
import net.cloudopt.next.grpc.test.example.VertxGreeterGrpc

class MyInterceptor : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        println("via MyInterceptor...")
        return next.startCall(call, headers)
    }
}


@GrpcService(interceptors = [MyInterceptor::class])
class HelloWordService : VertxGreeterGrpc.GreeterVertxImplBase() {
    override fun sayHello(request: HelloRequest): Future<HelloReply> {
        var helloReply: HelloReply = HelloReply.getDefaultInstance()
        global {
            helloReply = await<HelloReply> { promise ->
                promise.complete(
                    HelloReply.newBuilder()
                        .setMessage(request.name)
                        .build()
                )
            }
        }
        return Future.succeededFuture(helloReply)
    }
}