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
package net.cloudopt.next.eventbus.provider

import io.vertx.core.json.JsonObject
import net.cloudopt.next.eventbus.EventListener
import kotlin.reflect.KClass

interface EventBusProvider {

    /**
     * Initialize provider
     */
    suspend fun init()

    /**
     * Create a consumer and register it against the specified address.
     *
     * @param address  the address that will register it at
     * @param listener  the handler class that will process the received messages
     */
    suspend fun consumer(address: String, listener: KClass<EventListener>)

    /**
     * Sends a message.
     * <p>
     * The message will be delivered to at most one of the handlers registered to the address.
     *
     * @param address  the address to send it to
     * @param message  the json object of message, must not be {@code null}
     */
    suspend fun send(address: String, message: JsonObject)

    /**
     * Publish a message.<p>
     * The message will be delivered to all handlers registered to the address.
     *
     * @param address  the address to publish it to
     * @param message  the json object of message, must not be {@code null}
     *
     */
    suspend fun publish(address: String, message: JsonObject)

}