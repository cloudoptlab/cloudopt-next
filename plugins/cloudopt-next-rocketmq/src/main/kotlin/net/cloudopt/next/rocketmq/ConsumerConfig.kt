package net.cloudopt.next.rocketmq

import org.apache.rocketmq.common.UtilAll

data class ConsumerConfig(

    /**
     * Automatically changes to a orderly message queue when orderly = true.
     */
    var orderly: Boolean = false,

    /**
     * Producer group conceptually aggregates all producer instances of exactly same role, which is particularly
     * important when transactional messages are involved. For non-transactional messages, it does not matter as
     * long as it's unique per process. See for more discussion.
     */
    var groupName: String = "",

    /**
     * Rocket mq nameserver address.
     */
    var namesrvAddr: String = "",

    /**
     * Message model defines the way how messages are delivered to each consumer clients. RocketMQ supports two
     * message models: clustering and broadcasting. If clustering is set, consumer clients with the same consumerGroup
     * would only consume shards of the messages subscribed, which achieves load balances; Conversely, if the
     * broadcasting is set, each consumer client will consume all subscribed messages separately. This field defaults
     * to clustering.
     */
    var messageModel: String = "CLUSTERING",

    /**
     * Backtracking consumption time with second precision. Time format is 20131223171201 Implying Seventeen twelve and
     * 01 seconds on December 23, 2013 year Default backtracking consumption time Half an hour ago.
     */
    var consumeTimestamp: String = UtilAll.timeMillisToHumanString3(System.currentTimeMillis() - 1000 * 60 * 30),

    /**
     * Minimum consumer thread number
     */
    var consumeThreadMin: Int = 20,

    /**
     * Max consumer thread number
     */
    var consumeThreadMax: Int = 64,


    /**
     * Threshold for dynamic adjustment of the number of thread pool
     */
    var adjustThreadPoolNumsThreshold: Long = 100000,

    /**
     * Concurrently max span offset.it has no effect on sequential consumption
     */
    var consumeConcurrentlyMaxSpan: Int = 2000,

    /**
     * Flow control threshold on queue level, each message queue will cache at most 1000 messages by default,
     * Consider the `pullBatchSize`, the instantaneous value may exceed the limit
     */
    var pullThresholdForQueue: Int = 1000,

    /**
     * Limit the cached message size on queue level, each message queue will cache at most 100 MiB messages by default,
     * Consider the `pullBatchSize`, the instantaneous value may exceed the limit
     *
     *
     *
     * The size of a message only measured by message body, so it's not accurate
     */
    var pullThresholdSizeForQueue: Int = 100,

    /**
     * Flow control threshold on topic level, default value is -1(Unlimited)
     *
     *
     * The value of `pullThresholdForQueue` will be overwrote and calculated based on
     * `pullThresholdForTopic` if it is't unlimited
     *
     *
     * For example, if the value of pullThresholdForTopic is 1000 and 10 message queues are assigned to this consumer,
     * then pullThresholdForQueue will be set to 100
     */
    var pullThresholdForTopic: Int = -1,

    /**
     * Limit the cached message size on topic level, default value is -1 MiB(Unlimited)
     *
     *
     * The value of `pullThresholdSizeForQueue` will be overwrote and calculated based on
     * `pullThresholdSizeForTopic` if it is't unlimited
     *
     *
     * For example, if the value of pullThresholdSizeForTopic is 1000 MiB and 10 message queues are
     * assigned to this consumer, then pullThresholdSizeForQueue will be set to 100 MiB
     */
    var pullThresholdSizeForTopic: Int = -1,

    /**
     * Message pull Interval
     */
    var pullInterval: Long = 0,

    /**
     * Batch consumption size
     */
    var consumeMessageBatchMaxSize: Int = 1,

    /**
     * Batch pull size
     */
    var pullBatchSize: Int = 32,

    /**
     * Whether update subscription relationship when every pull
     */
    var postSubscriptionWhenPull: Boolean = false,

    /**
     * Whether the unit of subscription group
     */
    var unitMode: Boolean = false,

    /**
     * Max re-consume times. -1 means 16 times.
     *
     *
     * If messages are re-consumed more than [.maxReconsumeTimes] before success, it's be directed to a deletion
     * queue waiting.
     */
    var maxReconsumeTimes: Int = -1,

    /**
     * Suspending pulling time for cases requiring slow pulling like flow-control scenario.
     */
    var suspendCurrentQueueTimeMillis: Long = 1000,

    /**
     * Maximum amount of time in minutes a message may block the consuming thread.
     */
    var consumeTimeout: Long = 15,

    var accessKey: String = "",

    var accessSecret: String = ""
)