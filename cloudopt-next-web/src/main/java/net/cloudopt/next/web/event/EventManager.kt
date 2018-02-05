/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package net.cloudopt.next.web.event

import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Classer
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.json.Jsoner


/*
 * @author: Cloudopt
 * @Time: 2018/2/5
 * @Description: Used to manage events and send events.
 */
object EventManager {

    @JvmStatic
    open var eventBus: EventBus? = null

    @JvmStatic
    private val eventList: HashMap<String, EventListener> = hashMapOf()

    private val logger = Logger.Companion.getLogger(EventManager::class.java)

    fun init(vertx: Vertx) {
        // init event bus.
        eventBus = vertx.eventBus()

        Classer.scanPackageByAnnotation(CloudoptServer.packageName, false, AutoEvent::class.java)
                .forEach { clazz ->
                    eventList.put(clazz.getDeclaredAnnotation(AutoEvent::class.java).value, Beaner.newInstance(clazz))
                }

        eventList.keys.forEach { key ->
            eventBus?.consumer<Any>(key, { message ->
                eventList.get(key)?.listener(message)
            })?.completionHandler({ res ->
                if (res.succeeded()) {
                    logger.info("[EVENT] Registered event listener：[" + key + "] on" + eventList.get(key)!!::class.java.getName())
                } else {
                    logger.error("[EVENT] Registered event listener was error：" + eventList.get(key)!!::class.java.getName())
                }
            })
        }


    }

    fun send(name: String, obj: Object) {
        send(name, Jsoner.toJsonString(obj))
    }

    fun send(name: String, body: String) {
        eventBus?.send(name, body)
    }

    fun publish(name: String, obj: Object) {
        publish(name, Jsoner.toJsonString(obj))
    }

    fun publish(name: String, body: String) {
        eventBus?.publish(name, body)
    }


}