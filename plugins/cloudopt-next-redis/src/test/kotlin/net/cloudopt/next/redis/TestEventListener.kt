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
package net.cloudopt.next.redis

import io.lettuce.core.pubsub.RedisPubSubAdapter
import net.cloudopt.next.logging.Logger

class TestEventListener : RedisPubSubAdapter<String, String>() {

    companion object {
        @JvmStatic
        private val logger = Logger.getLogger(TestEventListener::class)
    }

    override fun message(channel: String?, message: String?) {
        logger.info("Got a new message on the $channel topic.")
    }

    override fun subscribed(channel: String?, count: Long) {
        logger.info("Subscribe $channel topic to redis pub/sub Success.")
    }


    override fun unsubscribed(channel: String?, count: Long) {
        logger.info("Unsubscribe $channel topic to redis pub/sub Success.")
    }

}