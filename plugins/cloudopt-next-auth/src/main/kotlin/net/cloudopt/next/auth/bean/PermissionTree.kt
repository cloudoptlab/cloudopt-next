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
package net.cloudopt.next.auth.bean

import java.util.*

/**
 * Permission tree for caching
 * @property roles LinkedList<Role>
 * @see Role
 * @property groups LinkedList<Group>
 * @see Group
 * @property users LinkedList<User>
 * @see User
 * @constructor
 */
data class PermissionTree(
    var roles: LinkedList<Role> = LinkedList(),
    var groups: LinkedList<Group> = LinkedList(),
    var users: LinkedList<User> = LinkedList()
)