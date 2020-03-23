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
import io.vertx.core.Vertx
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.kafka.client.producer.RecordMetadata
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.config.ConfigManager


/*
 * @author: Cloudopt
 * @Time: 2018/2/9
 * @Description: Kafka manager of cloudopt next
 */
object KafkaManager {

    private val logger = Logger.getLogger(KafkaManager::class.java)

    @JvmStatic
    private val kafkaList: HashMap<String, MutableSet<Class<*>>> = hashMapOf()

    @JvmStatic
    open var consumer: KafkaConsumer<Any, Any>? = null

    @JvmStatic
    open var producer: KafkaProducer<Any, Any>? = null

    @JvmStatic
    var config = ConfigManager.init("kafka") as MutableMap<String, String>

    fun init(vertx: Vertx) {
        consumer = KafkaConsumer.create<Any, Any>(vertx, config)?.exceptionHandler { e ->
            logger.error("[KAFKA] Consumer was error： ${e.message}")
        }
        producer = KafkaProducer.create<Any, Any>(vertx, config)?.exceptionHandler { e ->
            logger.error("[KAFKA] Producer was error： ${e.message}")
        }


        Classer.scanPackageByAnnotation(CloudoptServer.packageName, true, AutoKafka::class.java)
            .forEach { clazz ->
                clazz.getDeclaredAnnotation(AutoKafka::class.java).value.split(",").forEach { topic ->
                    var set = kafkaList.get(topic) ?: mutableSetOf()
                    set.add(clazz)
                    kafkaList.set(topic, set)
                }
            }



        consumer?.subscribe(kafkaList.keys) { ar ->
            if (ar.succeeded()) {
                logger.info("[KAFKA] Registered topic listener was success：${ar.cause()}")
            } else {
                logger.error("[KAFKA] Registered topic listener was error：${ar.cause()}")
            }
        }?.handler { record ->
            if (record.topic().isNotBlank() && kafkaList.get(record.topic())?.size ?: 0 > 0) {
                kafkaList.get(record.topic())?.forEach { clazz ->
                    var obj = Beaner.newInstance<KafkaListener>(clazz)
                    obj.listener(record as KafkaConsumerRecord<String, Any>)
                }
            }
        }


    }

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