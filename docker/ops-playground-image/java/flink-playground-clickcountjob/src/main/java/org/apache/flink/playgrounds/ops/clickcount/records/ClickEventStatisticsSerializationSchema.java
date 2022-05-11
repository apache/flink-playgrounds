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

package org.apache.flink.playgrounds.ops.clickcount.records;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * A Kafka {@link SerializationSchema} to serialize {@link ClickEventStatistics}s as JSON.
 *
 */
public class ClickEventStatisticsSerializationSchema implements SerializationSchema<ClickEventStatistics> {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public byte[] serialize(ClickEventStatistics event) {
		try {
			//if topic is null, default topic will be used
			return objectMapper.writeValueAsBytes(event);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Could not serialize record: " + event, e);
		}
	}
}
