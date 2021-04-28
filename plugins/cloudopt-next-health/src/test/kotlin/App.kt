import net.cloudopt.next.health.HealthChecksManager
import net.cloudopt.next.health.HealthChecksPlugin
import net.cloudopt.next.health.indicators.DiskSpaceHealthIndicator
import net.cloudopt.next.health.indicators.JvmHealthIndicator
import net.cloudopt.next.health.indicators.SystemIndicator
import net.cloudopt.next.web.NextServer

fun main() {
    NextServer.addPlugin(HealthChecksPlugin())
    HealthChecksManager.register("disk",DiskSpaceHealthIndicator())
    HealthChecksManager.register("jvm",JvmHealthIndicator())
    HealthChecksManager.register("system",SystemIndicator())
    NextServer.run()
}