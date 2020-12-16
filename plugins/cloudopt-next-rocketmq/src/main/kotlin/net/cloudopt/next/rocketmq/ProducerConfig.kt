package net.cloudopt.next.rocketmq

data class ProducerConfig(

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
     * Just for testing or demo program.
     */
    var createTopicKey: String = "AUTO_CREATE_TOPIC_KEY",

    /**
     * Maximum allowed message size in bytes.
     */
    var maxMessageSize: Int = 1024 * 1024 * 4,

    /**
     * Timeout for sending messages.
     */
    var sendMessageTimeOut: Int = 3000,

    /**
     * Number of queues to create per default topic.
     */
    var defaultTopicQueueNums: Int = 4,

    /**
     * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
     */
    var compressMsgBodyOverHowmuch: Int = 1024 * 4,

    /**
     * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    var retryTimesWhenSendFailed: Int = 2,

    /**
     * Indicate whether to retry another broker on sending failure internally.
     */
    var retryAnotherBrokerWhenNotStoreOK: Boolean = false,

    var accessKey: String = "",

    var accessSecret: String = ""
)
