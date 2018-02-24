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
import net.cloudopt.next.aop.Beaner
import net.cloudopt.next.aop.Classer
import net.cloudopt.next.logging.Logger
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import net.cloudopt.next.web.event.AutoEvent
import net.cloudopt.next.web.event.EventListener
import net.cloudopt.next.web.event.EventManager


/*
 * @author: Cloudopt
 * @Time: 2018/2/9
 * @Description: Kafka manager of cloudopt next
 */
object KafkaManager {

    private var map: MutableMap<String, String> = ConfigManager.initMap("kafka") as MutableMap<String, String>


    private val logger = Logger.Companion.getLogger(KafkaManager::class.java)

    @JvmStatic
    private val kafkaList: HashMap<String, MutableSet<Class<*>>> = hashMapOf()

    @JvmStatic
    open var consumer: KafkaConsumer<Any, Any>? = null

    @JvmStatic
    open var producer: KafkaProducer<Any, Any>? = null

    fun init(vertx: Vertx) {
        var config = mutableMapOf<String, String>()
        config["bootstrap.servers"] = map.get("servers") ?: ""
        config["key.deserializer"] = map.get("keyDeserializer") ?: "org.apache.kafka.common.serialization.StringDeserializer"
        config["value.deserializer"] = map.get("valueDeserializer") ?: "org.apache.kafka.common.serialization.StringDeserializer"
        config["group.id"] = map.get("groupId") ?: "cloudopt"
        config["auto.offset.reset"] = map.get("offsetRest") ?: "earliest"
        config["enable.auto.commit"] = map.get("autoCommit") ?: "false"
        config["key.serializer"] = map.get("keySerializer") ?: "org.apache.kafka.common.serialization.StringSerializer"
        config["value.serializer"] = map.get("valueSerializer") ?: "org.apache.kafka.common.serialization.StringSerializer"
        config["acks"] = map.get("acks") ?: "1"
        consumer = KafkaConsumer.create<Any, Any>(vertx, config)?.exceptionHandler({ e ->
            logger.error("[KAFKA] Consumer was error： ${e.message}")
        })
        producer = KafkaProducer.create<Any, Any>(vertx, config)?.exceptionHandler({ e ->
            logger.error("[KAFKA] Producer was error： ${e.message}")
        })


        Classer.scanPackageByAnnotation(CloudoptServer.packageName, false, AutoKafka::class.java)
                .forEach { clazz ->
                    clazz.getDeclaredAnnotation(AutoKafka::class.java).value.split(",").forEach { topic ->
                        var set = kafkaList.get(topic) ?: mutableSetOf()
                        set.add(clazz)
                        kafkaList.set(topic, set)
                    }
                }



            consumer?.subscribe(kafkaList.keys, { ar ->
                if (ar.succeeded()) {

                } else {
                    logger.error("[KAFKA] Registered topic listener was error：${ar.cause()}")
                }
            })?.handler({ record ->
                if (record.topic().isNotBlank() && kafkaList.get(record.topic())?.size ?: 0 > 0) {
                    kafkaList.get(record.topic())?.forEach { clazz ->
                        var obj = Beaner.newInstance<KafkaListener>(clazz)
                        obj.listener(record as KafkaConsumerRecord<String, Any>)
                    }
                }
            })




    }

    @JvmOverloads
    fun send(topic: String, key: String = "", value: String = "", partition: Int = 0, result: Handler<AsyncResult<RecordMetadata>>? = null) {
        var record = KafkaProducerRecord.create<Any, String>(topic, key, value, partition)
        if (result == null) {
            producer?.write(record as KafkaProducerRecord<Any, Any>)
        } else {
            producer?.write(record as KafkaProducerRecord<Any, Any>, result)
        }
    }

}