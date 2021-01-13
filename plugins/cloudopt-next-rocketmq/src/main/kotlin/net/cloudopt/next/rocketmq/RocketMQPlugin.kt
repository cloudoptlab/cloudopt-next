/*
 * Copyright 2017-2020 original authors
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


package net.cloudopt.next.rocketmq

import net.cloudopt.next.logging.Logger
import net.cloudopt.next.utils.Classer
import net.cloudopt.next.utils.Maper
import net.cloudopt.next.web.NextServer
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import org.apache.rocketmq.acl.common.AclClientRPCHook
import org.apache.rocketmq.acl.common.SessionCredentials
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly
import org.apache.rocketmq.client.producer.DefaultMQProducer
import org.apache.rocketmq.common.message.MessageExt
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation


object RocketMQManager {

    internal val logger = Logger.getLogger(RocketMQManager::class.java)

    @JvmStatic
    var producerConfig: ProducerConfig = ProducerConfig("", "")

    @JvmStatic
    var consumerConfig: ConsumerConfig = ConsumerConfig()

    @JvmStatic
    lateinit var producer: DefaultMQProducer

    @JvmStatic
    lateinit var consumer: DefaultMQPushConsumer

    /**
     * Store the scanned listeners.
     */
    @JvmStatic
    internal val listenerList: MutableMap<String, MutableMap<String, MutableSet<KClass<RocketMQListener>>>> = hashMapOf()

    init {
        if (ConfigManager.init("rocketmq.producer").isNotEmpty()) {
            logger.info("Detects the existence of a producer profile and creates a producer for rocketmq.")

            producerConfig =
                Maper.toObject(ConfigManager.init("rocketmq.producer"), ProducerConfig::class) as ProducerConfig
            producer = if (producerConfig.accessKey.isBlank()) {
                DefaultMQProducer(producerConfig.groupName)
            } else {
                DefaultMQProducer(
                    producerConfig.groupName, AclClientRPCHook(
                        SessionCredentials(
                            producerConfig.accessKey,
                            producerConfig.accessSecret
                        )
                    )
                )
            }

            producer.namesrvAddr = producerConfig.namesrvAddr
            producer.createTopicKey = producerConfig.createTopicKey
            producer.maxMessageSize = producerConfig.maxMessageSize
            producer.sendMsgTimeout = producerConfig.sendMessageTimeOut
            producer.defaultTopicQueueNums = producerConfig.defaultTopicQueueNums
            producer.compressMsgBodyOverHowmuch =
                producerConfig.compressMsgBodyOverHowmuch
            producer.retryTimesWhenSendFailed = producerConfig.retryTimesWhenSendFailed
            producer.isRetryAnotherBrokerWhenNotStoreOK =
                producerConfig.retryAnotherBrokerWhenNotStoreOK

        }

        if (ConfigManager.init("rocketmq.consumer").isNotEmpty()) {
            logger.info("Detects the existence of a consumer profile and creates a producer for rocketmq.")
            consumerConfig =
                Maper.toObject(ConfigManager.init("rocketmq.consumer"), ConsumerConfig::class) as ConsumerConfig
            consumer = if (consumerConfig.accessKey.isBlank()) {
                DefaultMQPushConsumer(consumerConfig.groupName)
            } else {
                DefaultMQPushConsumer(
                    null,
                    consumerConfig.groupName, AclClientRPCHook(
                        SessionCredentials(
                            producerConfig.accessKey,
                            producerConfig.accessSecret
                        )
                    )
                )
            }
            consumer.namesrvAddr = consumerConfig.namesrvAddr
            consumer.messageModel = if (consumerConfig.messageModel == "CLUSTERING") {
                MessageModel.CLUSTERING
            } else {
                MessageModel.BROADCASTING
            }
            consumer.consumeTimestamp = consumerConfig.consumeTimestamp
            consumer.consumeThreadMin = consumerConfig.consumeThreadMin
            consumer.consumeThreadMax = consumerConfig.consumeThreadMax
            consumer.adjustThreadPoolNumsThreshold =
                consumerConfig.adjustThreadPoolNumsThreshold
            consumer.consumeConcurrentlyMaxSpan =
                consumerConfig.consumeConcurrentlyMaxSpan
            consumer.pullThresholdForQueue = consumerConfig.pullThresholdForQueue
            consumer.pullThresholdSizeForQueue =
                consumerConfig.pullThresholdSizeForQueue
            consumer.pullThresholdForTopic = consumerConfig.pullThresholdForTopic
            consumer.pullThresholdSizeForTopic =
                consumerConfig.pullThresholdSizeForTopic
            consumer.pullInterval = consumerConfig.pullInterval
            consumer.consumeMessageBatchMaxSize =
                consumerConfig.consumeMessageBatchMaxSize
            consumer.pullBatchSize = consumerConfig.pullBatchSize
            consumer.isPostSubscriptionWhenPull =
                consumerConfig.postSubscriptionWhenPull
            consumer.isUnitMode = consumerConfig.unitMode
            consumer.maxReconsumeTimes = consumerConfig.maxReconsumeTimes
            consumer.suspendCurrentQueueTimeMillis =
                consumerConfig.suspendCurrentQueueTimeMillis
            consumer.consumeTimeout = consumerConfig.consumeTimeout
        }
    }

    /**
     * Distribute the received messages
     * @param msg Message
     */
    fun messageListener(msg: MessageExt) {
        val classSet = mutableSetOf<KClass<RocketMQListener>>()
        /**
         * Second put the listeners of all the tags under this topic into the set collection.
         */
        classSet.addAll(listenerList[msg.topic]?.get(msg.tags) ?: mutableSetOf())

        classSet.forEach {clazz->
            clazz.createInstance().listener(msg)
        }
    }

}

/**
 * @author: Cloudopt
 * @Time: 2020/12/15
 * @Description: RocketMQ plugin.
 * Apache RocketMQ is a distributed messaging and streaming platform with low latency, high performance and reliability,
 * trillion-level capacity and flexible scalability.
 */
class RocketMQPlugin : Plugin {

    private val logger = Logger.getLogger(RocketMQPlugin::class.java)

    override fun start(): Boolean {

        /**
         * Start scanning for annotations.
         */
        Classer.scanPackageByAnnotation(NextServer.packageName, true, AutoRocketMQ::class)
            .forEach { clazz ->
                clazz.findAnnotation<AutoRocketMQ>()?.value?.split(",")?.forEach { topic ->
                    val listenerMap = RocketMQManager.listenerList[topic] ?: mutableMapOf()

                    /**
                     * Split the tags and traverse the entire map
                     */
                    val tagList =
                        clazz.findAnnotation<AutoRocketMQ>()?.subExpression?.split("||") ?: mutableListOf<String>()
                    tagList.forEach { tag ->
                        val clazzSet = listenerMap[tag] ?: mutableSetOf()
                        clazzSet.add(clazz as KClass<RocketMQListener>)
                        listenerMap[tag] = clazzSet
                    }
                    RocketMQManager.listenerList[topic] = listenerMap
                }
            }


        if (ConfigManager.init("rocketmq.producer").isNotEmpty()) {
            RocketMQManager.producer.start()
        }
        if (ConfigManager.init("rocketmq.consumer").isNotEmpty()) {

            /**
             * Start auto subscribe.
             */
            RocketMQManager.listenerList.keys.forEach { topicName ->
                var subExpression = ""
                if (RocketMQManager.listenerList[topicName]?.keys?.contains("*") == true) {
                    subExpression = "*"
                } else {
                    RocketMQManager.listenerList[topicName]?.keys?.forEach { tag ->
                        if (subExpression.isNotBlank()){
                                subExpression = "$subExpression||$tag"
                        }else{
                            subExpression = tag
                        }
                    }
                }
                RocketMQManager.consumer.subscribe(topicName, subExpression)
                logger.info("[ROCKETMQ] Registered topic listener was success：topic = ${topicName}, subExpression = ${subExpression}")
            }

            if (RocketMQManager.consumerConfig.orderly) {
                /**
                 * Registering orderly message listeners.
                 */
                RocketMQManager.consumer.registerMessageListener(MessageListenerOrderly { msgs, context ->
                    msgs.forEach { msg ->
                        RocketMQManager.messageListener(msg)
                    }
                    ConsumeOrderlyStatus.SUCCESS
                })
            } else {
                /**
                 * Registering concurrent message listeners.
                 */
                RocketMQManager.consumer.registerMessageListener(MessageListenerConcurrently { msgs, context ->
                    msgs.forEach { msg ->
                        RocketMQManager.messageListener(msg)
                    }
                    ConsumeConcurrentlyStatus.CONSUME_SUCCESS
                })
            }

            RocketMQManager.consumer.start()
        }
        return true
    }

    override fun stop(): Boolean {
        if (ConfigManager.init("rocketmq.producer").isNotEmpty()) {
            RocketMQManager.producer.shutdown()
        }
        if (ConfigManager.init("rocketmq.consumer").isNotEmpty()) {
            RocketMQManager.consumer.shutdown()
        }
        return true
    }

}


