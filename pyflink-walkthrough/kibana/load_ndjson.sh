#!/bin/bash

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

# Wait for Kibana to start up before doing anything.
until curl -s http://kibana:5601/login -o /dev/null; do
    echo Waiting for Kibana...
    sleep 10
done

# The import service may not be ready, sleep 10s more.
sleep 20
curl -X POST kibana:5601/api/saved_objects/_import -H "kbn-xsrf: true" --form file=@/tmp/load/export.ndjson

echo 'all loaded up'
