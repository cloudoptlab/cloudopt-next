package net.cloudopt.next.grpc.test

import io.vertx.core.Future
import io.vertx.grpc.VertxChannelBuilder
import net.cloudopt.next.grpc.test.example.HelloReply
import net.cloudopt.next.grpc.test.example.HelloRequest
import net.cloudopt.next.grpc.test.example.VertxGreeterGrpc
import net.cloudopt.next.web.Worker

fun main() {
    val channel = VertxChannelBuilder
        .forAddress(Worker.vertx, "127.0.0.1", 9090)
        .usePlaintext()
        .build()
    val stub = VertxGreeterGrpc.newVertxStub(channel)
    val request = HelloRequest.newBuilder().setName("Next").build()


    val future: Future<HelloReply> = stub.sayHello(request)

    future
        .onSuccess { helloReply -> println("Got the server response: " + helloReply.message) }
        .onFailure { err -> println("Could not reach server $err") }
}