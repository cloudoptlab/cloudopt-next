/*
 * Copyright 2017 Cloudopt.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */


package net.cloudopt.next.redis

import net.cloudopt.next.redis.serializer.FstSerializer
import net.cloudopt.next.redis.serializer.ISerializer
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

import java.util.*

/*
 * @author: Cloudopt
 * @Time: 2018/2/8
 * @Description: Cache.
 * Cache api and Jedis api basically the same
 * Specific reference Redis file: http://redisdoc.com/
 */
class Cache {


    var name: String = ""
        protected set
    var jedisPool: JedisPool = JedisPool()
    var serializer: ISerializer = FstSerializer()
        protected set
    var keyNamingPolicy: IKeyNamingPolicy = IKeyNamingPolicy.defaultKeyNamingPolicy
        protected set

    protected val threadLocalJedis = ThreadLocal<Jedis>()

    val jedis: Jedis
        get() {
            val jedis = threadLocalJedis.get()
            return jedis ?: jedisPool.resource
        }

    protected constructor() {

    }

    constructor(name: String, jedisPool: JedisPool, serializer: ISerializer, keyNamingPolicy: IKeyNamingPolicy) {
        this.name = name
        this.jedisPool = jedisPool
        this.serializer = serializer
        this.keyNamingPolicy = keyNamingPolicy
    }

    operator fun set(key: Any, value: Any): String {
        val jedis = jedis
        try {
            return jedis.set(keyToBytes(key), valueToBytes(value))
        } finally {
            close(jedis)
        }
    }

    fun setex(key: Any, seconds: Int, value: Any): String {
        val jedis = jedis
        try {
            return jedis.setex(keyToBytes(key), seconds, valueToBytes(value))
        } finally {
            close(jedis)
        }
    }

    operator fun <T> get(key: Any): Any? {
        val jedis = jedis
        try {
            if (jedis.exists(keyToBytes(key))) {
                return valueFromBytes(jedis.get(keyToBytes(key)))
            } else {
                return null
            }
        } finally {
            close(jedis)
        }
    }

    fun del(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.del(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun del(vararg keys: Any): Long? {
        val jedis = jedis
        try {
            return jedis.del(*keysToBytesArray(*keys))
        } finally {
            close(jedis)
        }
    }

    fun keys(pattern: String): Set<String> {
        val jedis = jedis
        try {
            return jedis.keys(pattern)
        } finally {
            close(jedis)
        }
    }

    fun mset(vararg keysValues: Any): String {
        if (keysValues.size % 2 != 0)
            throw IllegalArgumentException("wrong number of arguments for met, keysValues length can not be odd")
        val jedis = jedis
        try {
            val kv = arrayOfNulls<ByteArray>(keysValues.size)
            for (i in keysValues.indices) {
                if (i % 2 == 0)
                    kv[i] = keyToBytes(keysValues[i])
                else
                    kv[i] = valueToBytes(keysValues[i])
            }
            return jedis.mset(*kv)
        } finally {
            close(jedis)
        }
    }

    fun mget(vararg keys: Any): List<*> {
        val jedis = jedis
        try {
            val keysBytesArray = keysToBytesArray(*keys)
            val data = jedis.mget(*keysBytesArray)
            return valueListFromBytesList(data)
        } finally {
            close(jedis)
        }
    }

    fun decr(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.decr(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun decrBy(key: Any, longValue: Long): Long? {
        val jedis = jedis
        try {
            return jedis.decrBy(keyToBytes(key), longValue)
        } finally {
            close(jedis)
        }
    }

    fun incr(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.incr(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun incrBy(key: Any, longValue: Long): Long? {
        val jedis = jedis
        try {
            return jedis.incrBy(keyToBytes(key), longValue)
        } finally {
            close(jedis)
        }
    }

    fun exists(key: Any): Boolean {
        val jedis = jedis
        try {
            return jedis.exists(keyToBytes(key))!!
        } finally {
            close(jedis)
        }
    }

    fun randomKey(): String {
        val jedis = jedis
        try {
            return jedis.randomKey()
        } finally {
            close(jedis)
        }
    }

    fun rename(oldkey: Any, newkey: Any): String {
        val jedis = jedis
        try {
            return jedis.rename(keyToBytes(oldkey), keyToBytes(newkey))
        } finally {
            close(jedis)
        }
    }

    fun move(key: Any, dbIndex: Int): Long? {
        val jedis = jedis
        try {
            return jedis.move(keyToBytes(key), dbIndex)
        } finally {
            close(jedis)
        }
    }

    fun migrate(host: String, port: Int, key: Any, destinationDb: Int, timeout: Int): String {
        val jedis = jedis
        try {
            return jedis.migrate(host, port, keyToBytes(key), destinationDb, timeout)
        } finally {
            close(jedis)
        }
    }

    fun select(databaseIndex: Int): String {
        val jedis = jedis
        try {
            return jedis.select(databaseIndex)
        } finally {
            close(jedis)
        }
    }

    fun expire(key: Any, seconds: Int): Long? {
        val jedis = jedis
        try {
            return jedis.expire(keyToBytes(key), seconds)
        } finally {
            close(jedis)
        }
    }

    fun expireAt(key: Any, unixTime: Long): Long? {
        val jedis = jedis
        try {
            return jedis.expireAt(keyToBytes(key), unixTime)
        } finally {
            close(jedis)
        }
    }

    fun pexpire(key: Any, milliseconds: Long): Long? {
        val jedis = jedis
        try {
            return jedis.pexpire(keyToBytes(key), milliseconds)
        } finally {
            close(jedis)
        }
    }

    fun pexpireAt(key: Any, millisecondsTimestamp: Long): Long? {
        val jedis = jedis
        try {
            return jedis.pexpireAt(keyToBytes(key), millisecondsTimestamp)
        } finally {
            close(jedis)
        }
    }

    fun <T> getSet(key: Any, value: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.getSet(keyToBytes(key), valueToBytes(value))) as T
        } finally {
            close(jedis)
        }
    }

    fun persist(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.persist(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun type(key: Any): String {
        val jedis = jedis
        try {
            return jedis.type(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun ttl(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.ttl(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun pttl(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.pttl(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun objectRefcount(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.objectRefcount(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun objectIdletime(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.objectIdletime(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun hset(key: Any, field: Any, value: Any): Long? {
        val jedis = jedis
        try {
            return jedis.hset(keyToBytes(key), fieldToBytes(field), valueToBytes(value))
        } finally {
            close(jedis)
        }
    }

    fun hmset(key: Any, hash: Map<Any, Any>): String {
        val jedis = jedis
        try {
            val para = HashMap<ByteArray, ByteArray>()
            for ((key1, value) in hash)
                para[fieldToBytes(key1)] = valueToBytes(value)
            return jedis.hmset(keyToBytes(key), para)
        } finally {
            close(jedis)
        }
    }

    fun <T> hget(key: Any, field: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.hget(keyToBytes(key), fieldToBytes(field))) as T
        } finally {
            close(jedis)
        }
    }

    fun hmget(key: Any, vararg fields: Any): List<*> {
        val jedis = jedis
        try {
            val data = jedis.hmget(keyToBytes(key), *fieldsToBytesArray(*fields))
            return valueListFromBytesList(data)
        } finally {
            close(jedis)
        }
    }

    fun hdel(key: Any, vararg fields: Any): Long? {
        val jedis = jedis
        try {
            return jedis.hdel(keyToBytes(key), *fieldsToBytesArray(*fields))
        } finally {
            close(jedis)
        }
    }

    fun hexists(key: Any, field: Any): Boolean {
        val jedis = jedis
        try {
            return jedis.hexists(keyToBytes(key), fieldToBytes(field))!!
        } finally {
            close(jedis)
        }
    }

    fun hgetAll(key: Any): Map<*, *> {
        val jedis = jedis
        try {
            val data = jedis.hgetAll(keyToBytes(key))
            val result = HashMap<Any, Any>()
            for ((key1, value) in data)
                result[fieldFromBytes(key1)] = valueFromBytes(value)
            return result
        } finally {
            close(jedis)
        }
    }

    fun hvals(key: Any): List<*> {
        val jedis = jedis
        try {
            val data = jedis.hvals(keyToBytes(key))
            return valueListFromBytesList(data)
        } finally {
            close(jedis)
        }
    }

    fun hkeys(key: Any): Set<Any> {
        val jedis = jedis
        try {
            val fieldSet = jedis.hkeys(keyToBytes(key))
            val result = HashSet<Any>()
            fieldSetFromBytesSet(fieldSet, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun hlen(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.hlen(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun hincrBy(key: Any, field: Any, value: Long): Long? {
        val jedis = jedis
        try {
            return jedis.hincrBy(keyToBytes(key), fieldToBytes(field), value)
        } finally {
            close(jedis)
        }
    }

    fun hincrByFloat(key: Any, field: Any, value: Double): Double? {
        val jedis = jedis
        try {
            return jedis.hincrByFloat(keyToBytes(key), fieldToBytes(field), value)
        } finally {
            close(jedis)
        }
    }

    fun <T> lindex(key: Any, index: Long): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.lindex(keyToBytes(key), index)) as T
        } finally {
            close(jedis)
        }
    }

    fun getCounter(key: Any): Long? {
        val jedis = jedis
        try {
            val ret = jedis.get(keyNamingPolicy.getKeyName(key)) as String
            return if (ret != null) java.lang.Long.parseLong(ret) else null
        } finally {
            close(jedis)
        }
    }

    fun llen(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.llen(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun <T> lpop(key: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.lpop(keyToBytes(key))) as T
        } finally {
            close(jedis)
        }
    }

    fun lpush(key: Any, vararg values: Any): Long? {
        val jedis = jedis
        try {
            return jedis.lpush(keyToBytes(key), *valuesToBytesArray(*values))
        } finally {
            close(jedis)
        }
    }

    fun lset(key: Any, index: Long, value: Any): String {
        val jedis = jedis
        try {
            return jedis.lset(keyToBytes(key), index, valueToBytes(value))
        } finally {
            close(jedis)
        }
    }

    fun lrem(key: Any, count: Long, value: Any): Long? {
        val jedis = jedis
        try {
            return jedis.lrem(keyToBytes(key), count, valueToBytes(value))
        } finally {
            close(jedis)
        }
    }

    fun lrange(key: Any, start: Long, end: Long): List<*> {
        val jedis = jedis
        try {
            val data = jedis.lrange(keyToBytes(key), start, end)
            return if (data != null) {
                valueListFromBytesList(data)
            } else {
                ArrayList<ByteArray>(0)
            }
        } finally {
            close(jedis)
        }
    }

    fun ltrim(key: Any, start: Long, end: Long): String {
        val jedis = jedis
        try {
            return jedis.ltrim(keyToBytes(key), start, end)
        } finally {
            close(jedis)
        }
    }

    fun <T> rpop(key: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.rpop(keyToBytes(key))) as T
        } finally {
            close(jedis)
        }
    }

    fun <T> rpoplpush(srcKey: Any, dstKey: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.rpoplpush(keyToBytes(srcKey), keyToBytes(dstKey))) as T
        } finally {
            close(jedis)
        }
    }

    fun rpush(key: Any, vararg values: Any): Long? {
        val jedis = jedis
        try {
            return jedis.rpush(keyToBytes(key), *valuesToBytesArray(*values))
        } finally {
            close(jedis)
        }
    }

    fun blpop(timeout: Int, vararg keys: Any): List<*> {
        val jedis = jedis
        try {
            val data = jedis.blpop(timeout, *keysToBytesArray(*keys))
            return keyValueListFromBytesList(data)
        } finally {
            close(jedis)
        }
    }

    fun brpop(timeout: Int, vararg keys: Any): List<*> {
        val jedis = jedis
        try {
            val data = jedis.brpop(timeout, *keysToBytesArray(*keys))
            return keyValueListFromBytesList(data)
        } finally {
            close(jedis)
        }
    }

    fun ping(): String {
        val jedis = jedis
        try {
            return jedis.ping()
        } finally {
            close(jedis)
        }
    }

    fun sadd(key: Any, vararg members: Any): Long? {
        val jedis = jedis
        try {
            return jedis.sadd(keyToBytes(key), *valuesToBytesArray(*members))
        } finally {
            close(jedis)
        }
    }

    fun scard(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.scard(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun <T> spop(key: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.spop(keyToBytes(key))) as T
        } finally {
            close(jedis)
        }
    }

    fun smembers(key: Any): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.smembers(keyToBytes(key))
            val result = HashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun sismember(key: Any, member: Any): Boolean {
        val jedis = jedis
        try {
            return jedis.sismember(keyToBytes(key), valueToBytes(member))!!
        } finally {
            close(jedis)
        }
    }

    fun sinter(vararg keys: Any): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.sinter(*keysToBytesArray(*keys))
            val result = HashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun <T> srandmember(key: Any): T {
        val jedis = jedis
        try {
            return valueFromBytes(jedis.srandmember(keyToBytes(key))) as T
        } finally {
            close(jedis)
        }
    }

    fun srandmember(key: Any, count: Int): List<*> {
        val jedis = jedis
        try {
            val data = jedis.srandmember(keyToBytes(key), count)
            return valueListFromBytesList(data)
        } finally {
            close(jedis)
        }
    }

    fun srem(key: Any, vararg members: Any): Long? {
        val jedis = jedis
        try {
            return jedis.srem(keyToBytes(key), *valuesToBytesArray(*members))
        } finally {
            close(jedis)
        }
    }

    fun sunion(vararg keys: Any): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.sunion(*keysToBytesArray(*keys))
            val result = HashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun sdiff(vararg keys: Any): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.sdiff(*keysToBytesArray(*keys))
            val result = HashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun zadd(key: Any, score: Double, member: Any): Long? {
        val jedis = jedis
        try {
            return jedis.zadd(keyToBytes(key), score, valueToBytes(member))
        } finally {
            close(jedis)
        }
    }

    fun zadd(key: Any, scoreMembers: Map<Any, Double>): Long? {
        val jedis = jedis
        try {
            val para = HashMap<ByteArray, Double>()
            for ((key1, value) in scoreMembers)
                para[valueToBytes(key1)] = value
            return jedis.zadd(keyToBytes(key), para)
        } finally {
            close(jedis)
        }
    }

    fun zcard(key: Any): Long? {
        val jedis = jedis
        try {
            return jedis.zcard(keyToBytes(key))
        } finally {
            close(jedis)
        }
    }

    fun zcount(key: Any, min: Double, max: Double): Long? {
        val jedis = jedis
        try {
            return jedis.zcount(keyToBytes(key), min, max)
        } finally {
            close(jedis)
        }
    }

    fun zincrby(key: Any, score: Double, member: Any): Double? {
        val jedis = jedis
        try {
            return jedis.zincrby(keyToBytes(key), score, valueToBytes(member))
        } finally {
            close(jedis)
        }
    }

    fun zrange(key: Any, start: Long, end: Long): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.zrange(keyToBytes(key), start, end)
            val result = LinkedHashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun zrevrange(key: Any, start: Long, end: Long): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.zrevrange(keyToBytes(key), start, end)
            val result = LinkedHashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun zrangeByScore(key: Any, min: Double, max: Double): Set<*> {
        val jedis = jedis
        try {
            val data = jedis.zrangeByScore(keyToBytes(key), min, max)
            val result = LinkedHashSet<Any>()
            valueSetFromBytesSet(data, result)
            return result
        } finally {
            close(jedis)
        }
    }

    fun zrank(key: Any, member: Any): Long? {
        val jedis = jedis
        try {
            return jedis.zrank(keyToBytes(key), valueToBytes(member))
        } finally {
            close(jedis)
        }
    }

    fun zrevrank(key: Any, member: Any): Long? {
        val jedis = jedis
        try {
            return jedis.zrevrank(keyToBytes(key), valueToBytes(member))
        } finally {
            close(jedis)
        }
    }

    fun zrem(key: Any, vararg members: Any): Long? {
        val jedis = jedis
        try {
            return jedis.zrem(keyToBytes(key), *valuesToBytesArray(*members))
        } finally {
            close(jedis)
        }
    }

    fun zscore(key: Any, member: Any): Double? {
        val jedis = jedis
        try {
            return jedis.zscore(keyToBytes(key), valueToBytes(member))
        } finally {
            close(jedis)
        }
    }

    protected fun keyToBytes(key: Any): ByteArray {
        val keyStr = keyNamingPolicy.getKeyName(key)
        return serializer.keyToBytes(keyStr)
    }

    protected fun keyFromBytes(bytes: ByteArray): Any {
        return serializer.keyFromBytes(bytes)
    }

    protected fun keysToBytesArray(vararg keys: Any): Array<ByteArray?> {
        val result = arrayOfNulls<ByteArray>(keys.size)
        for (i in result.indices)
            result[i] = keyToBytes(keys[i])
        return result
    }

    protected fun fieldToBytes(field: Any): ByteArray {
        return serializer.fieldToBytes(field)
    }

    protected fun fieldFromBytes(bytes: ByteArray): Any {
        return serializer.fieldFromBytes(bytes)
    }

    protected fun fieldsToBytesArray(vararg fieldsArray: Any): Array<ByteArray?> {
        val data = arrayOfNulls<ByteArray>(fieldsArray.size)
        for (i in data.indices)
            data[i] = fieldToBytes(fieldsArray[i])
        return data
    }

    protected fun fieldSetFromBytesSet(data: Set<ByteArray>, result: MutableSet<Any>) {
        for (fieldBytes in data) {
            result.add(fieldFromBytes(fieldBytes))
        }
    }

    protected fun valueToBytes(value: Any): ByteArray {
        return serializer.valueToBytes(value)
    }

    protected fun valueFromBytes(bytes: ByteArray): Any {
        return serializer.valueFromBytes(bytes)
    }

    protected fun valuesToBytesArray(vararg valuesArray: Any): Array<ByteArray?> {
        val data = arrayOfNulls<ByteArray>(valuesArray.size)
        for (i in data.indices)
            data[i] = valueToBytes(valuesArray[i])
        return data
    }

    protected fun valueSetFromBytesSet(data: Set<ByteArray>, result: MutableSet<Any>) {
        for (valueBytes in data) {
            result.add(valueFromBytes(valueBytes))
        }
    }

    protected fun valueListFromBytesList(data: List<ByteArray>): List<*> {
        val result = ArrayList<Any>()
        for (d in data)
            result.add(valueFromBytes(d))
        return result
    }

    protected fun keyValueListFromBytesList(data: List<ByteArray>): List<*> {
        val result = ArrayList<Any>()
        result.add(keyFromBytes(data[0]))
        result.add(valueFromBytes(data[1]))
        return result
    }

    fun close(jedis: Jedis?) {
        if (threadLocalJedis.get() == null && jedis != null)
            jedis.close()
    }

    fun getThreadLocalJedis(): Jedis {
        return threadLocalJedis.get()
    }

    fun setThreadLocalJedis(jedis: Jedis) {
        threadLocalJedis.set(jedis)
    }

    fun removeThreadLocalJedis() {
        threadLocalJedis.remove()
    }
}






