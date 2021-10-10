package net.cloudopt.next.web.test

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
import net.cloudopt.next.web.WebSocketResource
import net.cloudopt.next.web.annotation.WebSocket
import net.cloudopt.next.web.getCookie
import net.cloudopt.next.web.getIP

@WebSocket("/websocket")
class WebSocketHandler : WebSocketResource {

    override suspend fun onConnectionSuccess(websocket: ServerWebSocket) {
        websocket.writeTextMessage("Connection successful!") {
            println("The event of after write.")
        }

        val buffer: Buffer = Buffer.buffer().appendInt(123).appendFloat(1.23f)

        websocket.writeBinaryMessage(buffer) {
            println("The event of after write binary.")
        }
        websocket.getCookie("key")
        websocket.getIP()
    }

    override suspend fun onConnectionFailure(throwable: Throwable) {

    }

    override suspend fun onConnectionComplete(websocket: ServerWebSocket) {

    }

    override suspend fun onFrameMessage(frame: WebSocketFrame, websocket: ServerWebSocket) {

    }

    override suspend fun onTextMessage(message: String, websocket: ServerWebSocket) {
        println(message)

        websocket.writeTextMessage("This is the message from the server!")
    }

    override suspend fun onBinaryMessage(buffer: Buffer, websocket: ServerWebSocket) {

    }

    override suspend fun onPong(buffer: Buffer, websocket: ServerWebSocket) {

    }

    override suspend fun onException(throwable: Throwable, websocket: ServerWebSocket) {
        throwable.printStackTrace()

        if (!websocket.isClosed) {

            websocket.close()

        }
    }

    override suspend fun onDrain(websocket: ServerWebSocket) {

    }

    override suspend fun onEnd(websocket: ServerWebSocket) {
        println("Connection was closed.")
    }
}