package com.is.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

@SuppressWarnings("deprecation")
public class KafkaProducer extends Thread {

	private final Producer<Integer, String> producer;

	private final String topic;
	private final String message;

	private final Properties properties = new Properties();

	public KafkaProducer(String topic,String message) {
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("metadata.broker.list", "120.26.60.164:9092");
		producer = new Producer<Integer, String>(new ProducerConfig(properties));
		this.topic = topic;
		this.message=message;
	}
	
	  @Override
	    public void run() {
	        while (true)
	        {
	            System.out.println("Send:" + message);
	            producer.send(new KeyedMessage<Integer, String>(topic, message));
	            try {
	                sleep(3000);
	            } catch (InterruptedException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    }

}
