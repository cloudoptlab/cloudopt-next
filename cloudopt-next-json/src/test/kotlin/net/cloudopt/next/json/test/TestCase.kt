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

import io.vertx.core.json.JsonObject
import net.cloudopt.next.json.Jsoner.json
import net.cloudopt.next.json.Jsoner.jsonArray
import net.cloudopt.next.json.Jsoner.jsonToMutableMap
import net.cloudopt.next.json.Jsoner.jsonToMutableMapList
import net.cloudopt.next.json.Jsoner.jsonToObject
import net.cloudopt.next.json.Jsoner.jsonToObjectList
import net.cloudopt.next.json.Jsoner.toJsonArray
import net.cloudopt.next.json.Jsoner.toJsonObject
import net.cloudopt.next.json.Jsoner.toJsonString
import org.junit.Test
import kotlin.test.assertTrue

class TestCase {

    private val testJsonArrayString = "[{\"name\":\"Andy\",\"sex\":1},{\"name\":\"GitHub\",\"sex\":2}]"

    private val testJsonString = "{\"name\":\"Andy\",\"sex\":1}"

    @Test
    fun toJsonMap() {
        assertTrue {
            testJsonString.jsonToMutableMap()["name"]?.equals("Andy") ?: false
        }
    }

    @Test
    fun toJsonMapList() {
        assertTrue {
            testJsonArrayString.jsonToMutableMapList()[0]["name"]?.equals("Andy") ?: false
        }
    }

    @Test
    fun toJsonString() {
        assertTrue { Student("Andy", 1).toJsonString() == testJsonString }
    }

    @Test
    fun toJsonObject() {
        assertTrue {
            testJsonString.toJsonObject().getString("name") == "Andy"
        }
    }

    @Test
    fun toJsonArray() {
        assertTrue {
            testJsonArrayString.toJsonArray().getJsonObject(0).getString("name") == "Andy"
        }
    }

    @Test
    fun toObject() {
        val student: Student = testJsonString.jsonToObject(Student::class)
        assertTrue {
            student.name == "Andy"
        }
    }

    @Test
    fun toObjectList() {
        val student: List<Student> = testJsonArrayString.jsonToObjectList(Student::class)
        assertTrue {
            student[0].name == "Andy"
        }
    }

    @Test
    fun toList() {
        assertTrue {
            (testJsonArrayString.jsonToObjectList()[0] as JsonObject).getString("name") == "Andy"
        }
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
            "student" to Student(name = "next", sex = 1)
        )
        assert(
            a.toJsonString().isNotBlank()
        )
    }

}


