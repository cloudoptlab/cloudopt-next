/*
 * Copyright 2017-2020 Cloudopt.
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
package net.cloudopt.next.auth

import net.cloudopt.next.auth.bean.Group
import net.cloudopt.next.auth.bean.Role
import net.cloudopt.next.auth.bean.Rule
import net.cloudopt.next.auth.bean.User
import net.cloudopt.next.auth.utils.RuleMatch

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
        if (cache){
            refreshCache()
        }
    }

    /**
     * Used to refresh the cache.
     */
    fun refreshCache(){
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
                cacheMap.put(user.uniqueTag, tempRuleMap)
            }
        }
    }

    /**
     * Get all the roles.
     * @see Role
     * @return MutableList<Role>
     */
    abstract fun getRoles(): MutableList<Role>

    /**
     * Get the role by id.
     * @param roleId role id
     * @see Role
     * @return Role
     */
    abstract fun getRole(roleId: Int): Role?

    /**
     * Get all the groups.
     * @see Group
     * @return MutableList<Group>
     */
    abstract fun getGroups(): MutableList<Group>

    /**
     * Get the group by id.
     * @param groupId group id
     * @see Group
     * @return Group
     */
    abstract fun getGroup(groupId: Int): Group?

    /**
     * Get all the users.
     * @see User
     * @return MutableList<User>
     */
    abstract fun getUsers(): MutableList<User>

    /**
     * Get the user by id.
     * @param userId user id
     * @see User
     * @return User
     */
    abstract fun getUser(userId: Int): User?

    /**
     * Get the user by uniqueTag.
     * @param uniqueTag user uniqueTag
     * @see User
     * @return User
     */
    abstract fun getUser(uniqueTag: String): User?

    /**
     * Used to check if you have permission to pass
     * @param uniqueTag String
     * @param url String
     * @param method String
     * @return As long as there is a rule that matches, it returns True.
     * If there is no rule that matches, it returns False.
     */
    fun enforce(uniqueTag: String, url: String, method: String): Boolean {
        if (cacheMap.containsKey(uniqueTag)){
            return RuleMatch.ruleMatch(url,method,cacheMap[uniqueTag]?: mutableListOf())
        }else{
            var user: User = getUser(uniqueTag) ?: return false
            var tempRuleMap = cacheMap[user.uniqueTag] ?: user.rules
            for (roleId in user.rolesIdList) {
                getRole(roleId)?.rules?.let { tempRuleMap.addAll(it) }
            }
            for (groupId in user.groupsIdList) {
                getGroup(groupId)?.rules?.let { tempRuleMap.addAll(it) }
            }
            return RuleMatch.ruleMatch(url,method,tempRuleMap)
        }
    }

}