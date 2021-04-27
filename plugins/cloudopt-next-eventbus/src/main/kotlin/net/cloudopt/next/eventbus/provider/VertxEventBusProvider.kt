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

import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.eventbus.EventBusManager
import net.cloudopt.next.eventbus.EventListener
import net.cloudopt.next.eventbus.codec.JsonMessageCodec
import net.cloudopt.next.eventbus.codec.MapMessageCodec
import net.cloudopt.next.logging.test.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmName


class VertxEventBusProvider : EventBusProvider {

    companion object {
        private lateinit var eventBus: EventBus
        private val logger = Logger.getLogger(VertxEventBusProvider::class)
        private val options = DeliveryOptions().setCodecName("json")
    }

    override suspend fun init() {
        eventBus = Worker.vertx.eventBus()
        eventBus.registerCodec(MapMessageCodec())
        eventBus.registerCodec(JsonMessageCodec())
    }

    override suspend fun consumer(address: String, listener: KClass<EventListener>) {
        eventBus.consumer<JsonObject>(address) { message ->
            global{
                listener.createInstance().listener(message.body())
            }
        }.completionHandler { result ->
            if (result.succeeded()) {
                logger.info("[EVENT] Registered event listener：[$address] on ${EventBusManager.eventListenerList[address]?.jvmName}")
            } else {
                logger.error("[EVENT] Registered event listener was error： ${EventBusManager.eventListenerList[address]?.jvmName}")
            }
        }
    }

    override suspend fun send(address: String, message: JsonObject) {
        eventBus.send(address, message, options)
    }

    override suspend fun publish(address: String, message: JsonObject) {
        eventBus.publish(address, message, options)
    }

}