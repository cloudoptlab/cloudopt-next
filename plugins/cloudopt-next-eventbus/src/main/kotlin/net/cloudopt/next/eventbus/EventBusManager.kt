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

import io.vertx.core.json.JsonObject
import net.cloudopt.next.eventbus.provider.EventBusProvider
import net.cloudopt.next.logging.test.Logger
import kotlin.reflect.KClass


object EventBusManager {

    @JvmStatic
    var providers = mutableMapOf<String, EventBusProvider>()

    @JvmStatic
    val eventListenerList: MutableMap<String, KClass<EventListener>> = hashMapOf()

    private val logger = Logger.getLogger(EventBusManager::class)

    /**
     * Sends a message.
     * <p>
     * The message will be delivered to at most one of the handlers registered to the address.
     *
     * @param providerName the name of the implementation class of the specified eventbus
     * @param address  the address to send it to
     * @param message  the json object of message, must not be {@code null}
     */
    suspend fun send(providerName: String = "default", address: String, message: JsonObject) {
        providers[providerName]?.send(address, message)
    }


    /**
     * Publish a message.<p>
     * The message will be delivered to all handlers registered to the address.
     *
     * @param providerName the name of the implementation class of the specified eventbus
     * @param address  the address to publish it to
     * @param message  the json object of message, must not be {@code null}
     *
     */
    suspend fun publish(providerName: String = "default", address: String, message: JsonObject) {
        providers[providerName]?.publish(address, message)
    }

    /**
     * Register the listener into the list of listeners.
     *
     * @param address address  the address to listen it to
     * @param kclass KClass<EventListener>
     */
    fun registerListener(address: String, kclass: KClass<EventListener>) {
        eventListenerList[address] = kclass
    }

    /**
     * Remove the listener into the list of listeners.
     *
     * @param address address  the address to remove it to
     */
    fun removeListener(address: String) {
        eventListenerList.remove(address)
    }


}