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

import io.lettuce.core.pubsub.RedisPubSubAdapter
import io.vertx.core.json.JsonObject
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.core.Worker.global
import net.cloudopt.next.eventbus.EventBusManager
import net.cloudopt.next.eventbus.EventListener
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.redis.RedisManager
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class RedisMQProvider(private val redisName: String = "default") : EventBusProvider {
    override suspend fun init() {
        RedisManager.addListener(redisName, RedisEventBusListener())
    }

    override suspend fun consumer(address: String, listener: KClass<EventListener>) {
        RedisManager.subscribe(address)
    }

    override suspend fun send(address: String, message: JsonObject) {
        throw RuntimeException("Redis mq does not support individual consumption, only publish！")
    }

    override suspend fun publish(address: String, message: JsonObject) {
        await {
            RedisManager.publishSync(redisName, address, message.toJsonString())
        }
    }
}

private class RedisEventBusListener : RedisPubSubAdapter<String, String>() {

    companion object {
        @JvmStatic
        private val logger = Logger.getLogger(RedisEventBusListener::class)
    }

    override fun message(channel: String, message: String) {
        global {
            if (EventBusManager.eventListenerList.containsKey(channel)) {
                EventBusManager.eventListenerList[channel]?.createInstance()?.listener(message.toJsonObject())
            }
        }
    }

    override fun subscribed(channel: String, count: Long) {
        logger.info("Subscribe $channel topic to redis pub/sub Success.")
    }


    override fun unsubscribed(channel: String, count: Long) {
        logger.info("Unsubscribe $channel topic to redis pub/sub Success.")
    }

}