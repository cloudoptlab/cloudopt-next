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
package net.cloudopt.next.json

import org.junit.Test


/*
 * @author: Cloudopt
 * @Time: 2018/1/31
 * @Description: Test Case
 */

class TestCase {

    @Test
    fun testRead() {
        println(Jsoner.read("test1.json"))
    }

    @Test
    fun testReadToObj() {
        println(Jsoner.read("test1.json", "", Student::class.java))
    }

    @Test
    fun testReadByPrefix() {
        println(Jsoner.read("test2.json", "net.cloudopt.next"))
    }

    @Test
    fun toJsonString() {
        println(Jsoner.toJsonString(Student("Andy", 1)))
    }

    @Test
    fun toJsonArray() {
        println(Jsoner.toJsonMapList("[{\"name\":\"Andy\",\"sex\":1},{\"name\":\"Andy\",\"sex\":1}]"))
    }

    @Test
    fun toObjectList() {
        println(
            Jsoner.toObjectList(
                "[{\"name\":\"Andy\",\"sex\":1},{\"name\":\"Andy\",\"sex\":1}]",
                Student::class.java
            )
        )
    }

}


