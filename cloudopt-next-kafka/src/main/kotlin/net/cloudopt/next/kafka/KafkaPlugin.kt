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

import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.producer.KafkaProducer
import net.cloudopt.next.utils.Beaner
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.CloudoptServer
import net.cloudopt.next.web.Plugin
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import java.util.*


/*
 * @author: Cloudopt
 * @Time: 2018/2/6
 * @Description: Kafka plugin of cloudopt next
 */
class KafkaPlugin : Plugin {


    override fun start(): Boolean {
        KafkaManager.consumer =
            KafkaConsumer.create<Any, Any>(CloudoptServer.vertx, KafkaManager.config)?.exceptionHandler { e ->
                KafkaManager.logger.error("[KAFKA] Consumer was error： ${e.message}")
            }
        KafkaManager.producer =
            KafkaProducer.create<Any, Any>(CloudoptServer.vertx, KafkaManager.config)?.exceptionHandler { e ->
                KafkaManager.logger.error("[KAFKA] Producer was error： ${e.message}")
            }


        Classer.scanPackageByAnnotation(CloudoptServer.packageName, true, AutoKafka::class.java)
            .forEach { clazz ->
                clazz.getDeclaredAnnotation(AutoKafka::class.java).value.split(",").forEach { topic ->
                    var set = KafkaManager.kafkaList.get(topic) ?: mutableSetOf()
                    set.add(clazz)
                    KafkaManager.kafkaList.set(topic, set)
                }
            }

        if (KafkaManager.kafkaList.isNotEmpty()) {
            KafkaManager.consumer?.subscribe(KafkaManager.kafkaList.keys) { ar ->
                if (ar.succeeded()) {
                    KafkaManager.logger.info("[KAFKA] Registered topic listener was success：${KafkaManager.kafkaList.keys}")
                } else {
                    KafkaManager.logger.error("[KAFKA] Registered topic listener was error：${KafkaManager.kafkaList.keys}")
                }
            }?.handler { record ->
                if (record.topic().isNotBlank() && KafkaManager.kafkaList.get(record.topic())?.size ?: 0 > 0) {
                    KafkaManager.kafkaList.get(record.topic())?.forEach { clazz ->
                        var obj = Beaner.newInstance<KafkaListener>(clazz)
                        obj.listener(record as KafkaConsumerRecord<String, Any>)
                    }
                }
            }
        }

        /*
        Init kafka streams
         */
        if (KafkaManager.config.get("streams") == "true") {
            val streamsProps: Properties = KafkaManager.config.toProperties()
            if (!streamsProps.contains(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG)) {
                streamsProps[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
            }
            if (!streamsProps.contains(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG)) {
                streamsProps[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
            }
            KafkaManager.streams = KafkaStreams(KafkaManager.streamsTopology, streamsProps)
            KafkaManager.streams?.setUncaughtExceptionHandler { _: Thread, throwable: Throwable -> throwable.printStackTrace() }
            KafkaManager.streams?.start()
        }
        return true
    }

    override fun stop(): Boolean {
        KafkaManager.producer?.close()
        KafkaManager.consumer?.close()
        KafkaManager.streams?.close()
        return true
    }

}