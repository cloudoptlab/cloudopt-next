import jakarta.validation.constraints.NotBlank

data class AccountBean(
    @get:NotBlank
    var username: String = "",
    var password: String = ""
)