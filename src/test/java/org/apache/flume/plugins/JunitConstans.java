/*
 *  Copyright (c) 2013.09.06 BeyondJ2EE.
 *  * All right reserved.
 *  * http://beyondj2ee.github.com
 *  * This software is the confidential and proprietary information of BeyondJ2EE
 *  * , Inc. You shall not disclose such Confidential Information and
 *  * shall use it only in accordance with the terms of the license agreement
 *  * you entered into with BeyondJ2EE.
 *  *
 *  * Revision History
 *  * Author              Date                  Description
 *  * ===============    ================       ======================================
 *  *  beyondj2ee
 *
 */

package org.apache.flume.plugins;

/**
 * define constans for junit.
 * User: beyondj2ee
 * Date: 13. 9. 9
 * Time: AM 10:34
 */
public class JunitConstans {

    public static final String KAFKA_SERVER = "127.0.0.1:9092";
    public static final String TOPIC_NAME = "kafkaToptic";
    public static final String GROUP_ID = "testGroup";
    public static final String ZOOKEEPER_SERVER = "127.0.0.1:2181";
    public static final String MAX_CONSUMER_COUNT = "4";

    public static final String PARTITION_CLASS = "com.zqh.bigdata.flume_kafka.SinglePartition";
    public static final String KAFKASINK_CLASS = "com.zqh.bigdata.flume_kafka.KafkaSink";
    public static final String KAFKASOURCE_CLASS = "com.zqh.bigdata.flume_kafka.KafkaSource";
}
