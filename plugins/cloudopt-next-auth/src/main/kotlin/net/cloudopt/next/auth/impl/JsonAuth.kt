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
package net.cloudopt.next.auth.impl

import net.cloudopt.next.auth.Auth
import net.cloudopt.next.auth.bean.Group
import net.cloudopt.next.auth.bean.PermissionTree
import net.cloudopt.next.auth.bean.Role
import net.cloudopt.next.auth.bean.User
import net.cloudopt.next.core.ConfigManager

class JsonAuth(cache: Boolean = true) : Auth(cache) {

    companion object {
        var config: PermissionTree = ConfigManager.initObject("auth", PermissionTree::class) as PermissionTree
    }

    override suspend fun getRoles(): MutableList<Role> {
        return config.roles
    }

    override suspend fun getRole(roleId: Int): Role? {
        var roles = getRoles()
        for (role in roles) {
            if (role.id == roleId) return role
        }
        return null
    }

    override suspend fun getGroups(): MutableList<Group> {
        return config.groups
    }

    override suspend fun getGroup(groupId: Int): Group? {
        var groups = getGroups()
        for (group in groups) {
            if (group.id == groupId) return group
        }
        return null
    }

    override suspend fun getUsers(): MutableList<User> {
        return config.users
    }

    override suspend fun getUser(userId: Int): User? {
        var users = getUsers()
        for (user in users) {
            if (user.id == userId) return user
        }
        return null
    }

    override suspend fun getUser(uniqueTag: String): User? {
        var users = getUsers()
        for (user in users) {
            if (user.uniqueTag == uniqueTag) return user
        }
        return null
    }
}