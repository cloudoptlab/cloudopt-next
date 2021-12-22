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
package net.cloudopt.next.exposed.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.cloudopt.next.core.Worker
import net.cloudopt.next.core.Worker.await
import net.cloudopt.next.exposed.ExposedManager
import net.cloudopt.next.exposed.ExposedPlugin
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import kotlin.test.Test

object Users : IntIdTable() {
    val name = varchar("name", 50).index()
    val city = reference("city", Cities)
    val age = integer("age")
}

object Cities : IntIdTable() {
    val name = varchar("name", 50)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var city by City referencedOn Users.city
    var age by Users.age
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)

    var name by Cities.name
    val users by User referrersOn Users.city
}


class TestCase {

    private val plugin = ExposedPlugin()

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        plugin.start()
        Dispatchers.setMain(Worker.dispatcher())
        transaction(ExposedManager.databases["default"]) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Cities, Users)
        }
    }

    @ExperimentalCoroutinesApi
    @After
    fun clear() {
        plugin.stop()
        Dispatchers.resetMain()
        transaction(ExposedManager.databases["default"]) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(Users, Cities)
        }
    }


    @Test
    fun testCURD() {
        transaction(ExposedManager.databases["default"]) {
            addLogger(StdOutSqlLogger)
            val stPete = City.new {
                name = "St. Petersburg"
            }
            assert(City.all().toMutableList()[0].name.equals("St. Petersburg"))

            User.new {
                name = "a"
                city = stPete
                age = 5
            }

            assert(User.all().toMutableList()[0].city.name.equals("St. Petersburg"))

            User.findById(User.all().toMutableList()[0].id)?.name = "b"

            assert(User.all().toMutableList()[0].name.equals("b"))


            User.findById(User.all().toMutableList()[0].id)?.delete()

            assert(User.all().toMutableList().size == 0)
        }
    }

    @Test
    fun testAwaitCURD(): Unit = runBlocking {
        transaction(ExposedManager.databases["default"]) {
            addLogger(StdOutSqlLogger)
            val stPete = City.new {
                name = "St. Petersburg"
            }

            User.new {
                name = "a"
                city = stPete
                age = 5
            }
        }

        assert(testSelect()[0].name == "a")

    }

    private suspend fun testSelect(): MutableList<User> {

        return await {
            return@await transaction(ExposedManager.databases["default"]) {
                addLogger(StdOutSqlLogger)
                return@transaction User.all().toMutableList()

            }
        }
    }


}