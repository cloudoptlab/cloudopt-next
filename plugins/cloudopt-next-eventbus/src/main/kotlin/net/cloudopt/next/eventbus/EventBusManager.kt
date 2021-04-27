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
package net.cloudopt.next.eventbus

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.logging.test.Logger
import net.cloudopt.next.core.Classer
import net.cloudopt.next.eventbus.codec.MapMessageCodec
import net.cloudopt.next.eventbus.codec.JsonMessageCodec
import net.cloudopt.next.eventbus.provider.EventBusProvider
import net.cloudopt.next.eventbus.provider.VertxEventBusProvider
import net.cloudopt.next.web.NextServer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName


object EventBusManager {

    @JvmStatic
    lateinit var provider: EventBusProvider

    @JvmStatic
    val eventListenerList: MutableMap<String, KClass<EventListener>> = hashMapOf()

    private val logger = Logger.getLogger(EventBusManager::class)

    /**
     * Sends a message.
     * <p>
     * The message will be delivered to at most one of the handlers registered to the address.
     *
     * @param address  the address to send it to
     * @param message  the json object of message, must not be {@code null}
     */
    suspend fun send(address: String, message: JsonObject) {
        provider.send(address, message)
    }


    /**
     * Publish a message.<p>
     * The message will be delivered to all handlers registered to the address.
     *
     * @param address  the address to publish it to
     * @param message  the json object of message, must not be {@code null}
     *
     */
    suspend fun publish(address: String, message: JsonObject) {
        provider.publish(address, message)
    }



}