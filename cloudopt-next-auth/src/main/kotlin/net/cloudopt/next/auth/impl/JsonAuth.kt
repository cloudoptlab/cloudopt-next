package net.cloudopt.next.auth.impl

import net.cloudopt.next.auth.Auth
import net.cloudopt.next.auth.bean.Group
import net.cloudopt.next.auth.bean.PermissionTree
import net.cloudopt.next.auth.bean.Role
import net.cloudopt.next.auth.bean.User
import net.cloudopt.next.web.config.ConfigManager

var config: PermissionTree = ConfigManager.initObject("auth", PermissionTree::class.java) as PermissionTree

class JsonAuth(cache: Boolean = true) : Auth(cache) {

    override fun getRoles(): MutableList<Role> {
        return config.roles
    }

    override fun getRole(roleId: Int): Role? {
        var roles = getRoles()
        for (role in roles) {
            if (role.id == roleId) return role
        }
        return null
    }

    override fun getGroups(): MutableList<Group> {
        return config.groups
    }

    override fun getGroup(groupId: Int): Group? {
        var groups = getGroups()
        for (group in groups) {
            if (group.id == groupId) return group
        }
        return null
    }

    override fun getUsers(): MutableList<User> {
        return config.users
    }

    override fun getUser(userId: Int): User? {
        var users = getUsers()
        for (user in users) {
            if (user.id == userId) return user
        }
        return null
    }

    override fun getUser(uniqueTag: String): User? {
        var users = getUsers()
        for (user in users) {
            if (user.uniqueTag == uniqueTag) return user
        }
        return null
    }
}