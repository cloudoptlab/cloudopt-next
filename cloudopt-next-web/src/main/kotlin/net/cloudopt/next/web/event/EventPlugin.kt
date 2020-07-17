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

import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.Plugin


/*
 * @author: Cloudopt
 * @Time: 2018/2/5
 * @Description: A plugin for event management.
 */
class EventPlugin : Plugin {

    override fun start(): Boolean {
        EventManager.init(CloudoptServer.vertx)
        return true
    }

    override fun stop(): Boolean {
        EventManager.eventBus.close { result ->
            if (result.failed()) {
                println(result.cause())
            }
        }
        return true
    }

}