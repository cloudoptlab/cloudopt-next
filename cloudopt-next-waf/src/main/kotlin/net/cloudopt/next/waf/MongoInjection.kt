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
package net.cloudopt.next.waf

import java.net.URLEncoder

/*
 * @author: Cloudopt
 * @Time: 2018/1/5
 * @Description: Mongodb anti-injection module
 */
class MongoInjection : Filter {

    private val blackList = arrayOf(
        "{",
        "}",
        "\$ne",
        "\$gte",
        "\$gt",
        "\$lt",
        "\$lte",
        "\$in",
        "\$nin",
        "\$exists",
        "\$where",
        "tojson",
        "==",
        "db.",
        "\$where"
    )

    /**
     * @param value Pending content
     * @return
     * @Description MongoDB anti-injection
     */
    override fun filter(value: String): String {
        var temp = value
        for (s in blackList) {
            temp = temp.replace(s, URLEncoder.encode(s, "UTF-8"))
        }
        return temp
    }


}
