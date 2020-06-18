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

package org.apache.flink.playground.datagen;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.apache.flink.playground.datagen.model.Transaction;
import org.apache.flink.playground.datagen.model.TransactionSerializer;
import org.apache.flink.playground.datagen.model.TransactionSupplier;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;

/** Generates CSV transaction records at a rate */
public class Producer implements Runnable, AutoCloseable {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

  private volatile boolean isRunning;

  private final String brokers;

  private final String topic;

  public Producer(String brokers, String topic) {
    this.brokers = brokers;
    this.topic = topic;
    this.isRunning = true;
  }

  @Override
  public void run() {
    KafkaProducer<Long, Transaction> producer = new KafkaProducer<>(getProperties());

    Throttler throttler = new Throttler(100);

    TransactionSupplier transactions = new TransactionSupplier();

    while (isRunning) {

      Transaction transaction = transactions.get();

      long millis = transaction.timestamp.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

      ProducerRecord<Long, Transaction> record =
          new ProducerRecord<>(topic, null, millis, transaction.accountId, transaction);
      producer.send(record);

      try {
        throttler.throttle();
      } catch (InterruptedException e) {
        isRunning = false;
      }
    }

    producer.close();
  }

  @Override
  public void close() {
    isRunning = false;
  }

  private Properties getProperties() {
    final Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 0);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TransactionSerializer.class);

    return props;
  }
}
