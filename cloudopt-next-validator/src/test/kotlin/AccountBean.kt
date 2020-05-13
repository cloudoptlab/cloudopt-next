import net.cloudopt.next.validator.annotation.Chinese
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class AccountBean(
    @get:NotBlank
    @get:Chinese(true)
    var username:String = "",
    var password:String = ""
)