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
package net.cloudopt.next.web.event

import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.CloudoptServer


/*
 * @author: Cloudopt
 * @Time: 2018/2/5
 * @Description: Used to manage events and send events.
 */
object EventManager {

    @JvmStatic
    lateinit var eventBus: EventBus

    @JvmStatic
    private val eventList: MutableMap<String, Class<*>> = hashMapOf()

    private val logger = Logger.getLogger(EventManager::class.java)

    fun init(vertx: Vertx) {
        /**
         * Init event bus
         */
        eventBus = vertx.eventBus()

        Classer.scanPackageByAnnotation(CloudoptServer.packageName, true, AutoEvent::class.java)
            .forEach { clazz ->
                eventList[clazz.getDeclaredAnnotation(AutoEvent::class.java).value] = clazz
            }

        eventList.keys.forEach { key ->
            eventBus.consumer<Any>(key) { message ->
                eventList[key]?.let {
                    Beaner.newInstance<EventListener>(it)
                }?.listener(message)
            }?.completionHandler { res ->
                if (res.succeeded()) {
                    logger.info("[EVENT] Registered event listener：[$key] on ${eventList.get(key)?.name}")
                } else {
                    logger.error("[EVENT] Registered event listener was error： ${eventList.get(key)?.name}")
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
        send(name, Jsoner.toJsonString(obj))
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
     * Sends a message byu object.
     * The message will be delivered to at most one of the handlers registered to the topic.
     * @param name the topic to send it to
     * @param body the message, may be {@code null}
     */
    fun sendObject(name: String, body: Any) {
        eventBus.send(name, body)
    }

    /**
     * Publish a message.
     * The message will be delivered to all handlers registered to the topic.
     * @param name the topic to send it to
     * @param obj the message, may be {@code null}
     *
     */
    fun publish(name: String, obj: Any) {
        publish(name, Jsoner.toJsonString(obj))
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


}