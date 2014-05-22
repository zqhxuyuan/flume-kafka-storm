package com.zqh.bigdata.storm.wc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class SentenceSpout extends BaseRichSpout {

	private SpoutOutputCollector collector;
	private ConcurrentHashMap<UUID, Values> pending;
	
	private String[] sentences = { "my dog has fleas", "i like cold beverages",
			"the dog ate my homework", "don't have a cow man",
			"i don't think i like fleas" };
	private int index = 0;

	@Override
	public void nextTuple() {
		// Realibility in spouts
		Values values = new Values(sentences[index]);
		UUID msgId = UUID.randomUUID();
		this.pending.put(msgId, values);
		this.collector.emit(values, msgId);

		// only emits each sentence once
		/**
		if(index < sentences.length){
			this.collector.emit(new Values(sentences[index]));
			index ++;
		}
		*/
		
		// this.collector.emit(new Values(sentences[index]));
		index++;
		if (index >= sentences.length) {
			index = 0;
		}
		//Utils.waitForMillis(1);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void ack(Object msgId) {
		this.pending.remove(msgId);
	}

	public void fail(Object msgId) {
		this.collector.emit(this.pending.get(msgId), msgId);
	}

	@Override
	public void open(Map config, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		pending = new ConcurrentHashMap<UUID, Values>();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("sentence"));
	}

}
