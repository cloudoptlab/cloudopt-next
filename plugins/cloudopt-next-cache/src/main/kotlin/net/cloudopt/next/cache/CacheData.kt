package net.cloudopt.next.cache

import io.vertx.core.MultiMap

data class CacheData(var bodyString: String, var headers: MultiMap)