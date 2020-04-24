package net.cloudopt.next.web

import io.vertx.core.Launcher

object CloudoptLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        var args: Array<String> = arrayOf("run", "--redeploy=\"**/*.class\"", "--launcher-class=${args[0]}")
        Launcher().dispatch(args)
    }

}