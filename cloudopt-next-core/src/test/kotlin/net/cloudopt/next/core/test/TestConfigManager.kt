package net.cloudopt.next.core.test

import net.cloudopt.next.core.ConfigManager
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestConfigManager {

    @Test
    fun testInitConfigMap() {
        assertFalse {
            (ConfigManager.configMap["waf"] as MutableMap<String, Any>)["plus"] as Boolean
        }
        assertTrue {
            ConfigManager.configMap["cookieCors"] as Boolean
        }
    }

    @Test
    fun testInitWafConfigBean() {
        val config: WafConfigBean = ConfigManager.initObject("waf", WafConfigBean::class)
        assertFalse {
            config.plus
        }
        assertTrue {
            config.xss
        }
    }

}