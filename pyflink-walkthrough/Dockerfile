###############################################################################
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
###############################################################################

###############################################################################
# Build PyFlink Playground Image
###############################################################################

FROM apache/flink:1.13.1-scala_2.12-java8
ARG FLINK_VERSION=1.13.1

# Install pyflink
RUN set -ex; \
  apt-get update; \
  apt-get -y install python3; \
  apt-get -y install python3-pip; \
  apt-get -y install python3-dev; \
  ln -s /usr/bin/python3 /usr/bin/python; \
  ln -s /usr/bin/pip3 /usr/bin/pip; \
  apt-get update; \
  python -m pip install --upgrade pip; \
  pip install apache-flink==1.13.1; \
  pip install kafka-python;


# Download connector libraries
RUN wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-json/${FLINK_VERSION}/flink-json-${FLINK_VERSION}.jar; \
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka_2.12/${FLINK_VERSION}/flink-sql-connector-kafka_2.12-${FLINK_VERSION}.jar; \
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-elasticsearch7_2.12/${FLINK_VERSION}/flink-sql-connector-elasticsearch7_2.12-${FLINK_VERSION}.jar;


RUN echo "taskmanager.memory.jvm-metaspace.size: 512m" >> /opt/flink/conf/flink-conf.yaml;

WORKDIR /opt/flink
