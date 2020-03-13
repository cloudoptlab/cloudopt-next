/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
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
