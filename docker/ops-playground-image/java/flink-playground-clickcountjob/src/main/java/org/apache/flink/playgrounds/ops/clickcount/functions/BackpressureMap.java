/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.playgrounds.ops.clickcount.functions;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.playgrounds.ops.clickcount.records.ClickEvent;

import java.time.LocalTime;

/**
 * This MapFunction causes severe backpressure during even-numbered minutes.
 * E.g., from 10:12:00 to 10:12:59 it will only process 10 events/sec,
 * but from 10:13:00 to 10:13:59 events will pass through unimpeded.
 */
public class BackpressureMap implements MapFunction<ClickEvent, ClickEvent> {

	private boolean causeBackpressure() {
		return ((LocalTime.now().getMinute() % 2) == 0);
	}

	@Override
	public ClickEvent map(ClickEvent event) throws Exception {
		if (causeBackpressure()) {
			Thread.sleep(100);
		}

		return event;
	}

}
