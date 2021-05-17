/*
 * Copyright 2017-2021 Cloudopt
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
package net.cloudopt.next.web

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.WebSocketFrame

open interface WebSocketResource {

    /**
     * Used for operations before websocket is established.
     * @param resource Route resource
     * @see Resource
     * @return If false is returned, the websocket connection will be automatically interrupted
     */
    suspend fun beforeConnection(resource: Resource): Boolean

    /**
     * Add a handler to be notified of the connection succeeded result.
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onConnectionSuccess(websocket: ServerWebSocket)

    /**
     * Add a handler to be notified of the failed result.
     * @param throwable The base class for all errors and exceptions.
     * Only instances of this class can be thrown or caught
     */
    suspend fun onConnectionFailure(throwable: Throwable)

    /**
     * Add a handler to be notified of the result.
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onConnectionComplete(websocket: ServerWebSocket)

    /**
     * Set a frame handler on the connection. This handler will be called when frames are read on the connection.
     * @param frame A WebSocket frame that represents either text or binary data.
     * </ br>
     * A WebSocket message is composed of one or more WebSocket frames.
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onFrameMessage(frame: WebSocketFrame, websocket: ServerWebSocket)

    /**
     * Set a text message handler on the connection.
     * @param message Text message
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onTextMessage(message: String, websocket: ServerWebSocket)

    /**
     * Set a binary message handler on the connection.
     * @param buffer Most data is shuffled around inside Vert.x using buffers
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onBinaryMessage(buffer: Buffer, websocket: ServerWebSocket)

    /**
     * Set a pong frame handler on the connection.  This handler will be invoked every time a pong frame is received
     * on the server, and can be used by both clients and servers since the RFC 6455 <a href="https://tools.ietf.org/html/rfc6455#section-5.5.2">section 5.5.2</a> and <a href="https://tools.ietf.org/html/rfc6455#section-5.5.3">section 5.5.3</a> do not
     * specify whether the client or server sends a ping.
     * @param buffer Most data is shuffled around inside Vert.x using buffers
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onPong(buffer: Buffer, websocket: ServerWebSocket)

    /**
     * Set a exception handler on the connection.
     * @param throwable The base class for all errors and exceptions. Only instances of this class can be thrown or caught
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onException(throwable: Throwable, websocket: ServerWebSocket)

    /**
     * Set a drain handler on the stream. If the write queue is full, then the handler will be called when the write
     * queue is ready to accept buffers again. See {@link Pipe} for an example of this being used.
     *
     * <p> The stream implementation defines when the drain handler, for example it could be when the queue size has been
     * reduced to {@code maxSize / 2}.
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onDrain(websocket: ServerWebSocket)

    /**
     * Set a handler called when the operation completes.
     * @param websocket Represents a server side WebSocket
     */
    suspend fun onEnd(websocket: ServerWebSocket)

}
