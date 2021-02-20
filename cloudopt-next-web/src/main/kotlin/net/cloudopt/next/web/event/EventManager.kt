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
package net.cloudopt.next.web.event

import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.event.codec.MapMessageCodec
import net.cloudopt.next.web.event.codec.ObjectMessageCodec
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName


/*
 * @author: Cloudopt
 * @Time: 2018/2/5
 * @Description: Used to manage events and send events.
 */
object EventManager {

    @JvmStatic
    lateinit var eventBus: EventBus

    @JvmStatic
    val eventList: MutableMap<String, KClass<*>> = hashMapOf()

    private val logger = Logger.getLogger(EventManager::class)

    fun init(vertx: Vertx) {
        /**
         * Init event bus
         */
        eventBus = vertx.eventBus()
        eventBus.registerCodec(MapMessageCodec())
        eventBus.registerCodec(ObjectMessageCodec())

        Classer.scanPackageByAnnotation(NextServer.packageName, true, AutoEvent::class)
            .forEach { clazz ->
                eventList[clazz.findAnnotation<AutoEvent>()?.value!!] = clazz
            }

        eventList.keys.forEach { key ->
            eventBus.consumer<Any>(key) { message ->
                eventList[key]?.let {
                    it.createInstance() as EventListener
                }?.listener(message)
            }?.completionHandler { res ->
                if (res.succeeded()) {
                    logger.info("[EVENT] Registered event listener：[$key] on ${eventList?.get(key)?.jvmName}")
                } else {
                    logger.error("[EVENT] Registered event listener was error： ${eventList?.get(key)?.jvmName}")
                }
            }
        }


    }

    /**
     * Sends a message.
     * The message will be delivered to at most one of the handlers registered to the topic.
     * @param name the topic to send it to
     * @param obj the message, may be {@code null}
     */
    fun send(name: String, obj: Any) {
        send(name, obj.toJsonString())
    }

    /**
     * Sends a message.
     * The message will be delivered to at most one of the handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     */
    fun send(name: String, body: String) {
        eventBus.send(name, body)
    }

    /**
     * Sends a message by object.
     * The message will be delivered to at most one of the handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     */
    fun sendObject(name: String, body: Any) {
        sendObject(name, body, "object")
    }

    /**
     * Sends a message by object.
     * The message will be delivered to at most one of the handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     * @param codecName When sending or publishing a message a codec name can be provided. This must correspond with a previously registered
     * message codec. This allows you to send arbitrary objects on the event bus (e.g. POJOs).
     */
    fun sendObject(name: String, body: Any, codecName: String) {
        var options = DeliveryOptions()
        options.codecName = codecName
        eventBus.send(name, body, options)
    }

    /**
     * Publish a message.p
     * The message will be delivered to all handlers registered to the topic.
     * @param name the topic to send it to
     * @param obj the message, may be {@code null}
     *
     */
    fun publish(name: String, obj: Any) {
        publish(name, obj.toJsonString())
    }

    /**
     * Publish a message.
     * The message will be delivered to all handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     *
     */
    fun publish(name: String, body: String) {
        eventBus.publish(name, body)
    }

    /**
     * Publish a message by object.
     * The message will be delivered to all handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     */
    fun publishObject(name: String, body: Any) {
        publishObject(name, body, "object")
    }

    /**
     * Publish a message by object.
     * The message will be delivered to all handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     * @param codecName When sending or publishing a message a codec name can be provided. This must correspond with a previously registered
     * message codec. This allows you to send arbitrary objects on the event bus (e.g. POJOs).
     */
    fun publishObject(name: String, body: Any, codecName: String) {
        var options = DeliveryOptions()
        options.codecName = codecName
        eventBus.publish(name, body, options)
    }


}