package net.cloudopt.next.redis

data class RedisConfig(
    val name: String = "default",
    val uri: String = "redis://localhost",
    val cluster: Boolean = false,
    val publish: Boolean = false,
    val subscribe: Boolean = false
)
