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
package cloudopt.next.client

import net.cloudopt.next.json.Jsoner


/*
 * @author: Cloudopt
 * @Time: 2018/1/31
 * @Description: Test Case
 */


fun main(args: Array<String>) {

    var client = HttpClient("https://www.cloudopt.net").setPort(80)

    client.get("/api/v1/grade/website/www.google.com").send { result ->
        println(Jsoner.toJsonString(result.result().bodyAsString()))
    }
}

