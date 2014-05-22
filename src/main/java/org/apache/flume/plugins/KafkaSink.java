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
 * KAFKA Flume Sink (Kafka 0.8 Beta, Flume 1.4).
 * User: beyondj2ee
 * Date: 13. 9. 4
 * Time: PM 4:32
 *
 * 注释 by zqh @ 2014-05-14
 * flume-conf.properties的sink为KafkaSink. 说明数据从source到sink.
 * sink为消费端. 由于是Kafka, 所以要启动一个Producer, 将数据写到kafka队列中.
 */
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventHelper;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaSink extends AbstractSink implements Configurable {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSink.class);

    // flume配置以及上下文
    private Properties parameters;
    private Context context;

    // 生产者
    private Producer<String, String> producer;

    @Override
    public void configure(Context context) {
        this.context = context;
        ImmutableMap<String, String> props = context.getParameters();

        // base: producer.sinks.r
        parameters = new Properties();
        for (String key : props.keySet()) {
            String value = props.get(key);
            this.parameters.put(key, value);
        }
    }

    // 启动KafkaSink, 创建一个生产者
    @Override
    public synchronized void start() {
        super.start();
        ProducerConfig config = new ProducerConfig(this.parameters);
        this.producer = new Producer<String, String>(config);
    }

    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;

        // Start transaction 开启事务
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        txn.begin();
        try {
            // This try clause includes whatever Channel operations you want to do
            Event event = ch.take();

            String partitionKey = (String) parameters.get(KafkaFlumeConstans.PARTITION_KEY_NAME);
            String encoding = StringUtils.defaultIfEmpty(
                    (String) this.parameters.get(KafkaFlumeConstans.ENCODING_KEY_NAME),
                    KafkaFlumeConstans.DEFAULT_ENCODING);
            // flume-conf.properties中producer.sinks.r.custom.topic.name的配置项
            String topic = Preconditions.checkNotNull(
                    (String) this.parameters.get(KafkaFlumeConstans.CUSTOM_TOPIC_KEY_NAME),
                    "custom.topic.name is required");

            String eventData = new String(event.getBody(), encoding);

            KeyedMessage<String, String> data;

            // if partition key does'nt exist
            if (StringUtils.isEmpty(partitionKey)) {
                data = new KeyedMessage<String, String>(topic, eventData);
            } else {
                data = new KeyedMessage<String, String>(topic, partitionKey, eventData);
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Send Message to Kafka : [" + eventData + "] -- [" + EventHelper.dumpEvent(event) + "]");
            }
            // 生产者发送数据, 即往topic中写入数据
            producer.send(data);

            // 提交事务
            txn.commit();
            status = Status.READY;
        } catch (Throwable t) {
            txn.rollback();
            status = Status.BACKOFF;

            // re-throw all Errors
            if (t instanceof Error) {
                throw (Error) t;
            }
        } finally {
            txn.close();
        }
        return status;
    }

    @Override
    public void stop() {
        producer.close();
    }
}
