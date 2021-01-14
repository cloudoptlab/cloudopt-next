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
package net.cloudopt.next.web.test.validator

import io.vertx.kotlin.coroutines.awaitEvent
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.Resource
import net.cloudopt.next.web.Validator
import net.cloudopt.next.web.Worker


/*
 * @author: Cloudopt
 * @Time: 2018/2/28
 * @Description: Test Case
 */
class TestCoroutinesValidator : Validator {

    override suspend fun validate(resource: Resource): Boolean {
        var timeId = awaitEvent<Long> { handler ->
            Worker.setTimer(100, false, handler)
        }
        println("[TestCoroutinesValidator] Await event end! id=$timeId")
        return true
    }

    override suspend fun error(resource: Resource) {

    }


}