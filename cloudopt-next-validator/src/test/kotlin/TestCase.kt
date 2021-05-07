import net.cloudopt.next.validator.ValidatorTool
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class TestCase {

    @Test
    fun testValidate() {
        assertFalse {
            var account = AccountBean()
            ValidatorTool.validate(account).result
        }

        assertTrue {
            var account = AccountBean(username = "cloudopt")
            ValidatorTool.validate(account).result
        }
    }

    @Test
    fun testChinese() {
        assertFalse {
            val bean = CustomBean(name = "cloudopt", sex = "男")
            ValidatorTool.validate(bean).result
        }

        assertTrue {
            val bean = CustomBean(name = "测试", sex = "男")
            ValidatorTool.validate(bean).result
        }
    }

    @Test
    fun testInside() {
        assertTrue {
            val bean = CustomBean(name = "测试", sex = "男")
            ValidatorTool.validate(bean).result
        }

        assertFalse {
            val bean = CustomBean(name = "测试", sex = "未知")
            ValidatorTool.validate(bean).result
        }
    }

    @Test
    fun testType() {
        assertTrue {
            val bean = CustomBean(name = "测试", sex = "男", age = "1")
            ValidatorTool.validate(bean).result
        }

        assertFalse {
            val bean = CustomBean(name = "测试", sex = "未知", age = "0.111")
            ValidatorTool.validate(bean).result
        }
    }

}
