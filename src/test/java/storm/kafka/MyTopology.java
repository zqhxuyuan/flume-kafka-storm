package storm.kafka;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MyTopology {
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, InterruptedException, FileNotFoundException {
		BrokerHosts brokerHosts = new ZkHosts("localhost");
        // 2nd param: ZooKeeper install home
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, "kafkaToptic", "/usr/local/zookeeper-3.3.3", "word");

		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		//spoutConf.forceStartOffsetTime(-2);

		spoutConf.zkServers = new ArrayList<String>() {
			{
				add("localhost");
			}
		};
		spoutConf.zkPort = 2181;

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word-reader", new KafkaSpout(spoutConf), 3);
//		builder.setBolt("bolt", new PrintBolt()).shuffleGrouping("spout");
		builder.setBolt("word-normalizer", new WordNormalizer())
				.shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCounter(), 1).fieldsGrouping(
				"word-normalizer", new Fields("word"));

		Config conf = new Config();
		conf.put(Config.NIMBUS_HOST, "localhost");
//	    conf.put(Config.NIMBUS_THRIFT_PORT, 6627);
		conf.setNumWorkers(3);
		// conf.setDebug(true);

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);
			StormSubmitter.submitTopology("wordcountTopology", conf,
					builder.createTopology());
		} else {
            System.out.println("-----------------------");
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("local-host", conf, builder.createTopology());

			//Thread.sleep(30000);

			//cluster.shutdown();
		}
		Utils.sleep(10000);
	}
}
