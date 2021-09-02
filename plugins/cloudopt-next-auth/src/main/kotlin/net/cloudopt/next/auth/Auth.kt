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
package net.cloudopt.next.auth

import net.cloudopt.next.auth.bean.Group
import net.cloudopt.next.auth.bean.Role
import net.cloudopt.next.auth.bean.Rule
import net.cloudopt.next.auth.bean.User
import net.cloudopt.next.auth.utils.RuleMatch
import net.cloudopt.next.core.Worker.global

abstract class Auth {

    var cache = true

    var cacheMap: LinkedHashMap<String, MutableList<Rule>> = LinkedHashMap()

    /**
     * Initialize.
     * @param cache If set to true, it will automatically compile the relevant code into
     * hashMap to reduce the sh of search permissions.
     * @see net.cloudopt.next.auth.bean.PermissionTree
     */
    constructor(cache: Boolean = true) {
        this.cache = cache
        if (cache) {
            global{
                refreshCache()
            }
        }
    }

    /**
     * Used to refresh the cache.
     */
    suspend fun refreshCache() {
        var users = getUsers()
        for (user in users) {
            if (cache) {
                var tempRuleMap = cacheMap[user.uniqueTag] ?: user.rules
                for (roleId in user.rolesIdList) {
                    getRole(roleId)?.rules?.let { tempRuleMap.addAll(it) }
                }
                for (groupId in user.groupsIdList) {
                    getGroup(groupId)?.rules?.let { tempRuleMap.addAll(it) }
                }
                cacheMap[user.uniqueTag] = tempRuleMap
            }
        }
    }

    /**
     * Get all the roles.
     * @see Role
     * @return MutableList<Role>
     */
    abstract suspend fun getRoles(): MutableList<Role>

    /**
     * Get the role by id.
     * @param roleId role id
     * @see Role
     * @return Role
     */
    abstract suspend fun getRole(roleId: Int): Role?

    /**
     * Get all the groups.
     * @see Group
     * @return MutableList<Group>
     */
    abstract suspend fun getGroups(): MutableList<Group>

    /**
     * Get the group by id.
     * @param groupId group id
     * @see Group
     * @return Group
     */
    abstract suspend fun getGroup(groupId: Int): Group?

    /**
     * Get all the users.
     * @see User
     * @return MutableList<User>
     */
    abstract suspend fun getUsers(): MutableList<User>

    /**
     * Get the user by id.
     * @param userId user id
     * @see User
     * @return User
     */
    abstract suspend fun getUser(userId: Int): User?

    /**
     * Get the user by uniqueTag.
     * @param uniqueTag user uniqueTag
     * @see User
     * @return User
     */
    abstract suspend fun getUser(uniqueTag: String): User?

    /**
     * Used to check if you have permission to pass
     * @param uniqueTag String
     * @param url String
     * @param method String
     * @return As long as there is a rule that matches, it returns True.
     * If there is no rule that matches, it returns False.
     */
    suspend fun enforce(uniqueTag: String, url: String, method: String): Boolean {
        if (cacheMap.containsKey(uniqueTag)) {
            return RuleMatch.ruleMatch(url, method, cacheMap[uniqueTag] ?: mutableListOf())
        } else {
            var user: User = getUser(uniqueTag) ?: return false
            var tempRuleMap = cacheMap[user.uniqueTag] ?: user.rules
            for (roleId in user.rolesIdList) {
                getRole(roleId)?.rules?.let { tempRuleMap.addAll(it) }
            }
            for (groupId in user.groupsIdList) {
                getGroup(groupId)?.rules?.let { tempRuleMap.addAll(it) }
            }
            return RuleMatch.ruleMatch(url, method, tempRuleMap)
        }
    }

}