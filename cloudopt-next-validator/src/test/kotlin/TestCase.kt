import net.cloudopt.next.validator.ValidatorTool

class TestCase {


}

fun main() {
    var account = AccountBean()
    println(ValidatorTool.validate(account).result)
    println(ValidatorTool.validate(account).message)
}