################################################################################
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
# limitations under the License.
################################################################################

FROM maven:3.6-jdk-8-slim AS builder

COPY ./pom.xml /opt/pom.xml
COPY ./src /opt/src
RUN cd /opt; mvn clean install -Dmaven.test.skip

FROM flink:1.11-SNAPSHOT-scala_2.11

# Download connector libraries for snapshot version
RUN wget -P /opt/flink/lib/ https://repository.apache.org/content/repositories/snapshots/org/apache/flink/flink-sql-connector-kafka_2.11/1.11-SNAPSHOT/flink-sql-connector-kafka_2.11-1.11-20200610.034108-152.jar; \
    wget -P /opt/flink/lib/ https://repository.apache.org/content/repositories/snapshots/org/apache/flink/flink-connector-jdbc_2.11/1.11-SNAPSHOT/flink-connector-jdbc_2.11-1.11-20200610.033814-8.jar; \
    wget -P /opt/flink/lib/ https://repository.apache.org/content/repositories/snapshots/org/apache/flink/flink-csv/1.11-SNAPSHOT/flink-csv-1.11-20200610.033438-153.jar; \
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.19/mysql-connector-java-8.0.19.jar;


# Download connector libraries
#RUN wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka_2.11/${FLINK_VERSION}/flink-sql-connector-kafka_2.11-${FLINK_VERSION}.jar; \
#    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-jdbc_2.11/${FLINK_VERSION}/flink-jdbc_2.11-${FLINK_VERSION}.jar; \
#    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv/${FLINK_VERSION}/flink-csv-${FLINK_VERSION}.jar; \
#    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.19/mysql-connector-java-8.0.19.jar;

COPY --from=builder /opt/target/spend-report-*.jar /opt/flink/usrlib/spend-report.jar

RUN echo "execution.checkpointing.interval: 10s" >> /opt/flink/conf/flink-conf.yaml; \
    echo "pipeline.object-reuse: true" >> /opt/flink/conf/flink-conf.yaml; \
    echo "pipeline.time-characteristic: EventTime" >> /opt/flink/conf/flink-conf.yaml; \
    sed -i -e 's/taskmanager.memory.process.size: 1568m/taskmanager.memory.process.size: 1728m/g' /opt/flink/conf/flink-conf.yaml; \
    echo "taskmanager.memory.jvm-metaspace.size: 256m" >> /opt/flink/conf/flink-conf.yaml;
