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
package net.cloudopt.next.kafka

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.kafka.client.producer.RecordMetadata
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.config.ConfigManager
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.processor.AbstractProcessor
import org.apache.kafka.streams.processor.Processor
import java.util.*
import kotlin.collections.LinkedHashSet


/*
 * @author: Cloudopt
 * @Time: 2018/2/9
 * @Description: Kafka manager of cloudopt next
 */
object KafkaManager {

    val logger = Logger.getLogger(KafkaManager::class.java)

    @JvmStatic
    internal val kafkaList: MutableMap<String, MutableSet<Class<*>>> = hashMapOf()

    @JvmStatic
    open var consumer: KafkaConsumer<Any, Any>? = null

    @JvmStatic
    open var producer: KafkaProducer<Any, Any>? = null

    @JvmStatic
    open var streams: KafkaStreams? = null

    @JvmStatic
    open val streamsTopology = Topology()

    @JvmStatic
    var config = ConfigManager.init("kafka") as MutableMap<String, String>


    @JvmOverloads
    fun send(
        topic: String,
        key: String = "",
        value: String = "",
        partition: Int = 0,
        result: Handler<AsyncResult<RecordMetadata>>? = null
    ) {
        var record = KafkaProducerRecord.create<Any, String>(topic, key, value, partition)
        if (result == null) {
            producer?.write(record as KafkaProducerRecord<Any, Any>)
        } else {
            producer?.write(record as KafkaProducerRecord<Any, Any>)
        }
    }

}