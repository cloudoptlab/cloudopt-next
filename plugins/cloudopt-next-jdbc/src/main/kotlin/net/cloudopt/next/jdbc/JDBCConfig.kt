package net.cloudopt.next.jdbc

data class JDBCConfig(
    val name: String = "default",
    val database: String = "mysql",
    val pool: String = "net.cloudopt.next.jdbc.provider.HikariConnectionPoolProvider",
    val jdbcUrl:String = "",
    val username:String = "",
    val password:String = "",
    val driverClassName:String = "com.mysql.cj.jdbc.Driver"
)
