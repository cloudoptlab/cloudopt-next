import net.cloudopt.next.validator.annotation.Chinese
import net.cloudopt.next.validator.annotation.Inside
import net.cloudopt.next.validator.annotation.Type

data class CustomBean(
    @get:Chinese(true)
    val name: String = "",
    @get:Inside("男", "女")
    val sex: String = "",
    @get:Type("int")
    val age: String = "0"
)
