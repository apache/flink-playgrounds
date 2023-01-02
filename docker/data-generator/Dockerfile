# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM maven:3.8-eclipse-temurin-17 AS builder

# Get data producer code and compile it
COPY ./src /opt/data-producer/src
COPY ./pom.xml /opt/data-producer/pom.xml

RUN cd /opt/data-producer; \
    mvn clean verify

FROM eclipse-temurin:17

COPY --from=builder /opt/data-producer/target/data-generator-*.jar /opt/data-generator.jar

RUN cd /opt

COPY docker-entrypoint.sh /

ENTRYPOINT ["/docker-entrypoint.sh"]
