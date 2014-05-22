package com.zqh.bigdata.kafka.wiki;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
 
public abstract class SimplePartitioner implements Partitioner {
    public SimplePartitioner (VerifiableProperties props) {
 
    }
 
    public int partition(String key, int a_numPartitions) {
        int partition = 0;
        int offset = key.lastIndexOf('.');
        if (offset > 0) {
           partition = Integer.parseInt( key.substring(offset+1)) % a_numPartitions;
        }
       return partition;
  }
 
}