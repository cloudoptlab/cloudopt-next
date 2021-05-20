package net.cloudopt.next.core.test

import net.cloudopt.next.core.toMap
import net.cloudopt.next.core.toObject
import net.cloudopt.next.core.toProperties
import kotlin.test.Test
import kotlin.test.assertTrue

class TestMapper {

    @Test
    fun testToObject() {
        assertTrue {
            (mutableMapOf<String, Any>("name" to "cloudopt").toObject(Student::class) as Student).name == "cloudopt"
        }
    }


    @Test
    fun testToMap() {
        assertTrue {
            Student(name = "cloudopt").toMap()["name"] == "cloudopt"
        }
    }


    @Test
    fun testToProperties() {
        assertTrue {
            (mutableMapOf<String, Any>("name" to "cloudopt").toProperties()).getProperty("name") == "cloudopt"
        }
    }

}