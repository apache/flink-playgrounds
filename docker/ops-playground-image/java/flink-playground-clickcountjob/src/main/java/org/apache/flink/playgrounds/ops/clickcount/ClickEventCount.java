/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.playgrounds.ops.clickcount;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.playgrounds.ops.clickcount.functions.BackpressureMap;
import org.apache.flink.playgrounds.ops.clickcount.functions.ClickEventStatisticsCollector;
import org.apache.flink.playgrounds.ops.clickcount.functions.CountingAggregator;
import org.apache.flink.playgrounds.ops.clickcount.records.ClickEvent;
import org.apache.flink.playgrounds.ops.clickcount.records.ClickEventDeserializationSchema;
import org.apache.flink.playgrounds.ops.clickcount.records.ClickEventStatistics;
import org.apache.flink.playgrounds.ops.clickcount.records.ClickEventStatisticsSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * A simple streaming job reading {@link ClickEvent}s from Kafka, counting events per 15 seconds and
 * writing the resulting {@link ClickEventStatistics} back to Kafka.
 *
 * <p> It can be run with or without checkpointing and with event time or processing time semantics.
 * </p>
 *
 * <p>The Job can be configured via the command line:</p>
 * * "--checkpointing": enables checkpointing
 * * "--event-time": use an event time window assigner
 * * "--backpressure": insert an operator that causes periodic backpressure
 * * "--input-topic": the name of the Kafka Topic to consume {@link ClickEvent}s from
 * * "--output-topic": the name of the Kafka Topic to produce {@link ClickEventStatistics} to
 * * "--bootstrap.servers": comma-separated list of Kafka brokers
 *
 */
public class ClickEventCount {

	public static final String CHECKPOINTING_OPTION = "checkpointing";
	public static final String EVENT_TIME_OPTION = "event-time";
	public static final String BACKPRESSURE_OPTION = "backpressure";
	public static final String OPERATOR_CHAINING_OPTION = "chaining";

	public static final Time WINDOW_SIZE = Time.of(15, TimeUnit.SECONDS);

	public static void main(String[] args) throws Exception {
		final ParameterTool params = ParameterTool.fromArgs(args);

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		configureEnvironment(params, env);

		boolean inflictBackpressure = params.has(BACKPRESSURE_OPTION);

		String inputTopic = params.get("input-topic", "input");
		String outputTopic = params.get("output-topic", "output");
		String brokers = params.get("bootstrap.servers", "localhost:9092");
		Properties kafkaProps = new Properties();
		kafkaProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		kafkaProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "click-event-count");

		KafkaSource<ClickEvent> source = KafkaSource.<ClickEvent>builder()
				.setTopics(inputTopic)
				.setValueOnlyDeserializer(new ClickEventDeserializationSchema())
				.setProperties(kafkaProps)
				.build();

		WatermarkStrategy<ClickEvent> watermarkStrategy = WatermarkStrategy
				.<ClickEvent>forBoundedOutOfOrderness(Duration.ofMillis(200))
				.withTimestampAssigner((clickEvent, l) -> clickEvent.getTimestamp().getTime());

		DataStream<ClickEvent> clicks = env.fromSource(source, watermarkStrategy, "ClickEvent Source");

		if (inflictBackpressure) {
			// Force a network shuffle so that the backpressure will affect the buffer pools
			clicks = clicks
				.keyBy(ClickEvent::getPage)
				.map(new BackpressureMap())
				.name("Backpressure");
		}

		WindowAssigner<Object, TimeWindow> assigner = params.has(EVENT_TIME_OPTION) ?
				TumblingEventTimeWindows.of(WINDOW_SIZE) :
				TumblingProcessingTimeWindows.of(WINDOW_SIZE);

		DataStream<ClickEventStatistics> statistics = clicks
			.keyBy(ClickEvent::getPage)
			.window(assigner)
			.aggregate(new CountingAggregator(),
				new ClickEventStatisticsCollector())
			.name("ClickEvent Counter");

		statistics.sinkTo(
				KafkaSink.<ClickEventStatistics>builder()
						.setBootstrapServers(kafkaProps.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG))
						.setKafkaProducerConfig(kafkaProps)
						.setRecordSerializer(
								KafkaRecordSerializationSchema.builder()
										.setTopic(outputTopic)
										.setValueSerializationSchema(new ClickEventStatisticsSerializationSchema())
										.build())
						.setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
						.build())
				.name("ClickEventStatistics Sink");

		env.execute("Click Event Count");
	}

	private static void configureEnvironment(
			final ParameterTool params,
			final StreamExecutionEnvironment env) {

		boolean checkpointingEnabled = params.has(CHECKPOINTING_OPTION);
		boolean enableChaining = params.has(OPERATOR_CHAINING_OPTION);

		if (checkpointingEnabled) {
			env.enableCheckpointing(1000);
		}

		if(!enableChaining){
			//disabling Operator chaining to make it easier to follow the Job in the WebUI
			env.disableOperatorChaining();
		}
	}
}
