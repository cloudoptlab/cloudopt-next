/*
 * Copyright 2017-2021 Cloudopt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.kafka

import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.consumer.KafkaConsumerRecord
import io.vertx.kafka.client.producer.KafkaProducer
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.Worker
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import java.util.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation


/*
 * @author: Cloudopt
 * @Time: 2018/2/6
 * @Description: Kafka plugin of cloudopt next
 */
class KafkaPlugin : Plugin {


    override fun start(): Boolean {
        KafkaManager.consumer =
            KafkaConsumer.create<Any, Any>(Worker.vertx, KafkaManager.config)?.exceptionHandler { e ->
                KafkaManager.logger.error("[KAFKA] Consumer was error： ${e.message}")
            }
        KafkaManager.producer =
            KafkaProducer.create<Any, Any>(Worker.vertx, KafkaManager.config)?.exceptionHandler { e ->
                KafkaManager.logger.error("[KAFKA] Producer was error： ${e.message}")
            }


        Classer.scanPackageByAnnotation(NextServer.packageName, true, AutoKafka::class)
            .forEach { clazz ->
                clazz.findAnnotation<AutoKafka>()?.value?.split(",")?.forEach { topic ->
                    var set = KafkaManager.kafkaList[topic] ?: mutableSetOf()
                    set.add(clazz)
                    KafkaManager.kafkaList[topic] = set
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
                if (record.topic().isNotBlank() && KafkaManager.kafkaList[record.topic()]?.size ?: 0 > 0) {
                    KafkaManager.kafkaList[record.topic()]?.forEach { clazz ->
                        var obj = clazz.createInstance() as KafkaListener
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