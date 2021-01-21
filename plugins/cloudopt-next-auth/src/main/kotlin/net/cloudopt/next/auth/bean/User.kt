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
package net.cloudopt.next.auth.bean

import java.util.*

/**
 * Used to record verification users.
 * @property id user id
 * @property unique_tag unique ID of the user in the business system
 * @property rolesIdList list of role ids to which the user belongs
 * @property groupsIdList list of group ids to which the user belongs
 * @property rules LinkedList<Rule>
 * @see Rule
 * @constructor
 */
data class User(
    var id: Int = 0,
    var uniqueTag: String = "",
    var rolesIdList: List<Int> = mutableListOf(),
    var groupsIdList: List<Int> = mutableListOf(),
    var rules: LinkedList<Rule> = LinkedList()
)