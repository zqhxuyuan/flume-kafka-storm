package org.apache.flume.plugins;

/**
 * Kafka Simple Partitioner.
 * User: beyondj2ee
 * Date: 13. 9. 4
 * Time: PM 5:39
 */
import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * The type Single partition.
 */
public abstract class SinglePartition implements Partitioner {

    public SinglePartition(VerifiableProperties props) {
    }

    /**
     * choose only one partition.
     * @param key partition key
     * @param numberOfPartions number of partitions
     * @return the int
     */
    public int partition(String key, int numberOfPartions) {
        return 0;
    }

}
