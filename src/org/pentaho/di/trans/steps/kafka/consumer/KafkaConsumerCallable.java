/*******************************************************************************
 *
 * pentaho-kafka-consumer-plugin
 *
 * Copyright (C) 2011-2016 by Sun : http://www.kingbase.com.cn
 *
 *******************************************************************************
 *
 *
 *    Email : snj1314@163.com
 *
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.kafka.consumer;

import kafka.consumer.ConsumerTimeoutException;
import kafka.message.MessageAndMetadata;

import java.util.concurrent.Callable;

import org.pentaho.di.core.exception.KettleException;

/**
 * 
 * 
 * @author Sun
 * @since 2016-3-15
 * @version
 * 
 */
public abstract class KafkaConsumerCallable implements Callable<Object> {

	private KafkaConsumerData data;
	private KafkaConsumerMeta meta;
	private KafkaConsumer step;

	public KafkaConsumerCallable(KafkaConsumerMeta meta, KafkaConsumerData data, KafkaConsumer step) {
		this.meta = meta;
		this.data = data;
		this.step = step;
	}

	/**
	 * Called when new message arrives from Kafka stream
	 * 
	 * @param message
	 *            Kafka message
	 * @param key
	 *            Kafka key
	 */
	protected abstract void messageReceived(byte[] key, byte[] message) throws KettleException;

	public Object call() throws KettleException {
		try {
			long limit = meta.getLimit();
			if (limit > 0) {
				step.logDebug("Collecting up to " + limit + " messages");
			} else {
				step.logDebug("Collecting unlimited messages");
			}
			while (data.streamIterator.hasNext() && !data.canceled && (limit <= 0 || data.processed < limit)) {
				MessageAndMetadata<byte[], byte[]> messageAndMetadata = data.streamIterator.next();
				messageReceived(messageAndMetadata.key(), messageAndMetadata.message());
				++data.processed;
			}
		} catch (ConsumerTimeoutException cte) {
			step.logDebug("Received a consumer timeout after " + data.processed + " messages");
			if (!meta.isStopOnEmptyTopic()) {
				// Because we're not set to stop on empty, this is an abnormal
				// timeout
				throw new KettleException("Unexpected consumer timeout!", cte);
			}
		}
		// Notify that all messages were read successfully
		data.consumer.commitOffsets();
		step.setOutputDone();
		return null;
	}

}
