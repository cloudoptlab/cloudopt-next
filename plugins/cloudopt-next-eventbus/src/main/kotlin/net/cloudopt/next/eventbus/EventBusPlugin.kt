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

import net.cloudopt.next.core.Classer
import net.cloudopt.next.core.Plugin
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.eventbus.provider.EventBusProvider
import net.cloudopt.next.eventbus.provider.VertxEventBusProvider
import net.cloudopt.next.web.NextServer
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


class EventBusPlugin : Plugin {

    override fun start(): Boolean {
        Classer.scanPackageByAnnotation(NextServer.packageName, true, AutoEvent::class)
            .forEach { clazz ->
                val address = clazz.findAnnotation<AutoEvent>()?.value ?: ""
                if (address.isNotBlank()) {
                    EventBusManager.eventListenerList[address] = clazz as KClass<EventListener>
                }
            }

        global {
            if (!EventBusManager.providers.containsKey("default")) {
                EventBusManager.providers["default"] = VertxEventBusProvider()
            }
            EventBusManager.providers.values.forEach { provider: EventBusProvider ->
                provider.init()
                EventBusManager.eventListenerList.forEach { (address, kclass) ->
                    provider.consumer(address, kclass)
                }
            }
        }
        return true
    }

    override fun stop(): Boolean {
        return true
    }

}