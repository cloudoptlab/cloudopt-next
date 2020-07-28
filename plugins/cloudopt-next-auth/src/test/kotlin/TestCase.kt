import net.cloudopt.next.auth.Auth
import net.cloudopt.next.auth.bean.Group
import net.cloudopt.next.auth.bean.Role
import net.cloudopt.next.auth.bean.Rule
import net.cloudopt.next.auth.bean.User
import net.cloudopt.next.auth.impl.JsonAuth
import net.cloudopt.next.auth.utils.BuiltinOperators
import net.cloudopt.next.auth.utils.RuleMatch
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestCase {

    @Test
    fun creatModels() {
        var rules: LinkedList<Rule> = LinkedList<Rule>()
        rules.push(Rule(name = "about somethigs", url = "/api", method = "GET", allow = true))
        Role(id = 1, name = "2", rules = rules)
        Group(id = 1, name = "2", rules = rules)
        var model =
            User(
                id = 1,
                uniqueTag = "x-x-x-x",
                rolesIdList = mutableListOf(1),
                groupsIdList = mutableListOf(1),
                rules = rules
            )
        println(model)
    }

    @Test
    fun urlMatch() {
        println(BuiltinOperators.keyMatch2("/api/v1/account/*", "/api/v1/account"))
        println(BuiltinOperators.keyMatch2("/api/v1/account", "/api/v1/*"))
    }

    @Test
    fun creatAuthority() {
        var authority: Auth = JsonAuth(cache = true)
        println(authority)
    }

    @Test
    fun findRole() {
        var authority: Auth = JsonAuth(cache = true)
        print(authority.getRole(1))
    }

    @Test
    fun findGroup() {
        var authority: Auth = JsonAuth(cache = true)
        print(authority.getGroup(1))
    }

    @Test
    fun findUser() {
        var authority: Auth = JsonAuth(cache = true)
        print(authority.getUser(1))
        print(authority.getUser("SHUH-OSJI-UHIN-UUHG"))
    }

    @Test
    fun actionMatch() {
        var authority: Auth = JsonAuth()
        assertTrue(RuleMatch.ruleMatch("/api/v1/account/creat", "GET", authority.getRoles()[0].rules[0]))
        assertFalse(RuleMatch.ruleMatch("/api/v1/account/creat", "POST", authority.getRoles()[0].rules[0]))
        assertFalse(RuleMatch.ruleMatch("/api/v1/push", "POST", authority.getRoles()[0].rules[0]))
    }

    @Test
    fun defaultEnforce() {
        var authority: Auth = JsonAuth()
        assertTrue(authority.enforce("SHUH-OSJI-UHIN-UUHG", "/api/v1/account/creat", "GET"))
        assertFalse(authority.enforce("SHUH-OSJI-UHIN-UUHG", "/api/v1/account/creat", "POST"))
        assertFalse(authority.enforce("SHUH-OSJI-UHIN-UUHG", "/api/v1/push", "POST"))

        assertFalse(authority.enforce("s", "/api/v1/account/creat", "GET"))

        authority = JsonAuth(cache = false)
        assertTrue(authority.enforce("SHUH-OSJI-UHIN-UUHG", "/api/v1/account/creat", "GET"))
        assertFalse(authority.enforce("SHUH-OSJI-UHIN-UUHG", "/api/v1/account/creat", "POST"))
        assertFalse(authority.enforce("SHUH-OSJI-UHIN-UUHG", "/api/v1/push", "POST"))

        assertFalse(authority.enforce("s", "/api/v1/account/creat", "GET"))
    }

}