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
package net.cloudopt.next.json.test

import com.fasterxml.jackson.module.kotlin.jsonMapper
import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.json.Jsoner.jsonArray
import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.json.Jsoner.jsonToObjectList
import net.cloudopt.next.json.Jsoner.jsontoMutableMap
import net.cloudopt.next.json.Jsoner.jsontoMutableMapList
import net.cloudopt.next.json.Jsoner.jsontoObjectList
import net.cloudopt.next.json.Jsoner.toJsonArray
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import org.junit.Test

class TestCase {

    val testJsonArrayString = "[{\"name\":\"Andy\",\"sex\":1},{\"name\":\"GitHub\",\"sex\":2}]"

    val testJsonString = "{\"name\":\"Andy\",\"sex\":1}"

    @Test
    fun toJsonMap() {
        println(testJsonString.jsontoMutableMap())
    }

    @Test
    fun toJsonMapList() {
        println(testJsonArrayString.jsontoMutableMapList())
    }

    @Test
    fun toJsonString() {
        println(Student("Andy", 1).toJsonString())
    }

    @Test
    fun toJsonObject() {
        println(testJsonString.toJsonObject())
    }

    @Test
    fun toJsonArray() {
        println(testJsonArrayString.toJsonArray())
    }

    @Test
    fun toObject() {
        println(
            testJsonString.jsonToObject(Student::class)
        )
    }

    @Test
    fun toObjectList() {
        println(
            testJsonArrayString.jsonToObjectList(Student::class)
        )
    }

    @Test
    fun toList() {
        println(
            testJsonArrayString.jsontoObjectList()
        )
    }

    @Test
    fun creatJsonByDSL() {
        val a = json(
            "a" to "1",
            "b" to json(
                "c" to "2"
            ),
            "c" to jsonArray(
                json(
                    "d" to "3"
                )
            ),
        )
        println(a.toJsonString())
    }
}


