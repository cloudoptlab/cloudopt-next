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
package net.cloudopt.next.rocketmq.test

import net.cloudopt.next.rocketmq.RocketMQManager
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.route.API
import net.cloudopt.next.web.route.GET
import org.apache.rocketmq.client.producer.SendResult
import org.apache.rocketmq.common.message.Message

import org.apache.rocketmq.remoting.common.RemotingHelper





/*
 * @author: Cloudopt
 * @Time: 2018/1/26
 * @Description: Test Controller
 */
@API("/")
class IndexController : Resource() {

    @GET("event")
    fun event() {
        val msg = Message(
            "test-topic" /* Topic */,
            "TagA" /* Tag */,
            "Hello RocketMQ".toByteArray(charset = Charsets.UTF_8)
        )
        val sendResult: SendResult = RocketMQManager.producer.send(msg)
        RocketMQManager.producer.send(msg)
        renderJson("Send Event!")
    }
}