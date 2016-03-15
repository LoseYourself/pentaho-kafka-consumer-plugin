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

import kafka.consumer.ConsumerIterator;
import kafka.javaapi.consumer.ConsumerConnector;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * 
 * 
 * @author Sun
 * @since 2016-3-15
 * @version
 * 
 */
public class KafkaConsumerData extends BaseStepData implements StepDataInterface {

	ConsumerConnector consumer;
	ConsumerIterator<byte[], byte[]> streamIterator;
	RowMetaInterface outputRowMeta;
	RowMetaInterface inputRowMeta;
	boolean canceled;
	int processed;

}
