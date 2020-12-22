/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.web.test.controller

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.WebSocketResource
import net.cloudopt.next.web.route.WebSocket


/*
 * @author: Cloudopt
 * @Time: 2020/7/19
 * @Description: Test WebSocket Controller
 */

@WebSocket("/websocket")
class WebSocketHandler : WebSocketResource {

    override fun beforeConnection(resource: Resource): Boolean {
        resource.request.response().statusCode = 400
        resource.request.response().end()
        return false
    }

    override fun onConnectionSuccess(websocket: ServerWebSocket) {
        websocket.writeTextMessage("Connection successful!") {
            println("The event of after write.")
        }
        val buffer: Buffer = Buffer.buffer().appendInt(123).appendFloat(1.23f)
        websocket.writeBinaryMessage(buffer) {
            println("The event of after write binary.")
        }
    }

    override fun onConnectionFailure(throwable: Throwable) {

    }

    override fun onConnectionComplete(websocket: ServerWebSocket) {

    }

    override fun onFrameMessage(frame: WebSocketFrame, websocket: ServerWebSocket) {
        println(frame.textData())
        websocket.writeTextMessage("This is the message from the server!")
    }

    override fun onTextMessage(message: String, websocket: ServerWebSocket) {
        println(message)
        websocket.writeTextMessage("This is the message from the server!")
    }

    override fun onBinaryMessage(buffer: Buffer, websocket: ServerWebSocket) {

    }

    override fun onPingPong(buffer: Buffer, websocket: ServerWebSocket) {
        println("Pong...")
    }

    override fun onException(throwable: Throwable, websocket: ServerWebSocket) {
        throwable.printStackTrace()
        if (!websocket.isClosed) {
            websocket.close()
        }
    }

    override fun onDrain(websocket: ServerWebSocket) {

    }

    override fun onEnd(websocket: ServerWebSocket) {
        println("Connection was closed.")
    }


}