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
package net.cloudopt.next.rocketmq.test

import net.cloudopt.next.rocketmq.AutoRocketMQ
import net.cloudopt.next.rocketmq.RocketMQListener
import org.apache.rocketmq.common.message.MessageExt


/*
 * @author: Cloudopt
 * @Time: 2021/01/13
 * @Description: Test Case
 */
@AutoRocketMQ("test-topic", subExpression = "TagA||TagB")
class TestTagAandBListener : RocketMQListener {

    override fun listener(msg: MessageExt) {
        println("[TagA||TagB] Receive New Messages: $msg")
    }


}