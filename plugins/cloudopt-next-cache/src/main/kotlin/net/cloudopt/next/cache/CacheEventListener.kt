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
package net.cloudopt.next.cache

import io.lettuce.core.pubsub.RedisPubSubAdapter
import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.logging.test.Logger
import net.cloudopt.next.core.Worker

class CacheEventListener : RedisPubSubAdapter<String, String>() {

    companion object {
        @JvmStatic
        private val logger = Logger.getLogger(CacheEventListener::class)
    }

    override fun message(channel: String?, message: String?) {
        if (channel == CacheManager.CHANNELS) {
            logger.debug("Receive delete cache event message. $message")
            val messageObject: CacheEventMessage = message?.jsonToObject(CacheEventMessage::class)
                    as CacheEventMessage
            if (messageObject.regionName.isBlank() && messageObject.key.isBlank()) {
                logger.error("Region's name or cache key must not be null or blank!")
                return
            }
            Worker.global {
                when (messageObject.event) {
                    "DELETE_CACHE" -> {
                        CacheManager.delete(messageObject.regionName, messageObject.key, false)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun subscribed(channel: String?, count: Long) {
        logger.info("Subscribe ${CacheManager.CHANNELS} topic to redis pub/sub Success.")
    }


    override fun unsubscribed(channel: String?, count: Long) {
        logger.info("Unsubscribe ${CacheManager.CHANNELS} topic to redis pub/sub Success.")
    }

}