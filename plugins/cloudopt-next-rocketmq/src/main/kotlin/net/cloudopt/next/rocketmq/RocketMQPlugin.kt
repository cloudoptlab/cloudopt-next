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
import net.cloudopt.next.utils.Maper
import net.cloudopt.next.web.Plugin
import net.cloudopt.next.web.config.ConfigManager
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer
import org.apache.rocketmq.client.producer.DefaultMQProducer
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel

object RocketMQManager {

    private val logger = Logger.getLogger(RocketMQManager::class.java)

    @JvmStatic
    var producerConfig: ProducerConfig = ProducerConfig("", "")

    @JvmStatic
    var consumerConfig: ConsumerConfig = ConsumerConfig("")

    @JvmStatic
    lateinit var producer: DefaultMQProducer

    @JvmStatic
    lateinit var consumer: DefaultMQPushConsumer

    init {
        if (ConfigManager.init("rocketmq.producer").isNotEmpty()) {
            logger.info("Detects the existence of a producer profile and creates a producer for rocketmq.")

            producerConfig =
                Maper.toObject(ConfigManager.init("rocketmq.producer"), ProducerConfig::class.java) as ProducerConfig
            producer = DefaultMQProducer(producerConfig.groupName)
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
                Maper.toObject(ConfigManager.init("rocketmq.consumer"), ConsumerConfig::class.java) as ConsumerConfig
            consumer = DefaultMQPushConsumer(consumerConfig.groupName)
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
        if (ConfigManager.init("rocketmq.producer").isNotEmpty()) {
            RocketMQManager.producer.start()
        }
        if (ConfigManager.init("rocketmq.consumer").isNotEmpty()) {
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


