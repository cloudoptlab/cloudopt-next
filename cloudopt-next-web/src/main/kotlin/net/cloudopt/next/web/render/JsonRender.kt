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
package net.cloudopt.next.web.render

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.json.Jsoner.toJsonString
import net.cloudopt.next.web.Resource

class JsonRender : Render {

    override suspend fun render(resource: Resource, obj: Any) {
        val json = await<String> { promise ->
            try {
                promise.complete(obj.toJsonString())
            } catch (e: Exception) {
                promise.fail(e)
                e.printStackTrace()
                resource.fail(500, e)
                return@await
            }
        }
        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
        end(resource, json)
    }
}
