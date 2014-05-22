package com.zqh.bigdata.flume.guide;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;

/**
 * http://flume.apache.org/FlumeDeveloperGuide.html
 * How to run this app?
 *  1. config avro-demo.conf in $FLUME_HOME/conf
        a1.channels = c1
        a1.sources = r1
        a1.sinks = k1

        a1.channels.c1.type = memory

        a1.sources.r1.channels = c1
        a1.sources.r1.type = avro
        a1.sources.r1.bind = 0.0.0.0
        a1.sources.r1.port = 41414

        a1.sinks.k1.channel = c1
        a1.sinks.k1.type = logger

 *  2. start flume agent at $FLUME_HOME
 *    $ flume-ng agent --conf conf --conf-file conf/avro-demo.conf --name a1 -Dflume.root.logger=INFO,console
 *  
 *  3. start flume avro-client on a new terminal or run this app on eclipse
 *  3.1 terminal command:
 *    $ flume-ng avro-client -H localhost -p 41414 -F /var/log/syslog
 *  3.2 just run this app
 *  
 *  different between 3.1 and 3.2 is the data source we fetch. 
 *  3.1 data source from file /var/log/syslog
 *  3.2 data source from demo: 10 times Hello Flume!
 *  
 *  4. after u do one of step 3, u can see some output on step2's terminal
 *  
 */
public class MyApp {
	public static void main(String[] args) {
		MyRpcClientFacade client = new MyRpcClientFacade();
		// Initialize client with the remote Flume agent's host and port
		//client.init("host.example.org", 41414);
		client.init("localhost", 41414);

		// Send 10 events to the remote Flume agent. That agent should be configured to listen with an AvroSource.
		String sampleData = "Hello Flume!";
		for (int i = 0; i < 10; i++) {
			client.sendDataToFlume(sampleData);
		}

		client.cleanUp();
	}
}

class MyRpcClientFacade {
	private RpcClient client;
	private String hostname;
	private int port;

	public void init(String hostname, int port) {
		// Setup the RPC connection
		this.hostname = hostname;
		this.port = port;
		this.client = RpcClientFactory.getDefaultInstance(hostname, port);
		// Use the following method to create a thrift client (instead of the above line):
		// this.client = RpcClientFactory.getThriftInstance(hostname, port);
	}

	public void sendDataToFlume(String data) {
		// Create a Flume Event object that encapsulates the sample data
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));

		// Send the event
		try {
			client.append(event);
		} catch (EventDeliveryException e) {
			// clean up and recreate the client
			client.close();
			client = null;
			client = RpcClientFactory.getDefaultInstance(hostname, port);
			// Use the following method to create a thrift client (instead of
			// the above line):
			// this.client = RpcClientFactory.getThriftInstance(hostname, port);
		}
	}

	public void cleanUp() {
		// Close the RPC connection
		client.close();
	}

}