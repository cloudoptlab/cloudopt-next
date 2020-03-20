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


