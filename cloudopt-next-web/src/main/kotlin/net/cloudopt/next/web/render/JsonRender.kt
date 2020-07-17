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
package net.cloudopt.next.web.render

import io.vertx.core.http.HttpHeaders
import net.cloudopt.next.json.Jsoner
import net.cloudopt.next.web.Resource

/*
 * @author: Cloudopt
 * @Time: 2018/1/9
 * @Description: JsonProvider Render
 */
class JsonRender : Render {

    override fun render(resource: Resource, obj: Any) {

        var json = Jsoner.toJsonString(obj)

        resource.response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")

        end(resource, json)

    }
}
