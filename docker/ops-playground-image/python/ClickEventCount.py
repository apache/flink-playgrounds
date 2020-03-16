
# This is the python counterpart of ClickEventCount.java in the Flink Ops Playground example
# pyflink stream connector modified from 
# https://blog.csdn.net/wc188996/article/details/103081107
# https://blog.csdn.net/ghostyusheng/article/details/102696867

import os
 
from pyflink.datastream import StreamExecutionEnvironment, TimeCharacteristic
from pyflink.table import StreamTableEnvironment, CsvTableSink, DataTypes
from pyflink.table.descriptors import Schema, Rowtime, Json, Kafka
from pyflink.table.window import Tumble

s_env = StreamExecutionEnvironment.get_execution_environment()
s_env.set_parallelism(1)
s_env.set_stream_time_characteristic(TimeCharacteristic.EventTime)

# Stream Table
st_env = StreamTableEnvironment.create(s_env)

# Set source Kafka table
st_env \
    .connect(  # declare the external system to connect to
        Kafka() 
            .version("0.11")
            .topic("input")
            .start_from_earliest()
            .property("zookeeper.connect", "zookeeper:2181")
            .property("bootstrap.servers", "kafka:9092")
    ) \
    .with_format(  # declare a format for this system
        Json() 
            .fail_on_missing_field(True)
            .json_schema(
            "{"
            "  type: 'object',"
            "  properties: {"
            "    timestamp: {"
            "      type: 'string'"
            "    },"
            "    page: {"
            "      type: 'string'"
            "    }"
            "  }"
            "}"
        )
    ) \
    .with_schema(  # declare the schema of the table
        Schema() 
            .field("timestamp", DataTypes.TIMESTAMP()).proctime()
            .field("page", DataTypes.STRING())
    ) \
    .in_append_mode() \
    .register_table_source("ClickEvent Source")

# set output Kafka table
st_env \
    .connect(  # declare the external system to connect to
        Kafka() 
            .version("0.11")
            .topic("output")
            # .start_from_earliest()
            .property("zookeeper.connect", "zookeeper:2181")
            .property("bootstrap.servers", "kafka:9092")
    ) \
    .with_format(  # declare a format for this system
        Json() 
            .fail_on_missing_field(True)
            .json_schema(
            "{"
            "  type: 'object',"
            "  properties: {"
            "    windowStart: {"
            "      type: 'string'"
            "    },"
            "    windowEnd: {"
            "      type: 'string'"
            "    },"
            "    page: {"
            "      type: 'string'"
            "    },"
            "    count: {"
            "      type: 'number'"
            "    }"
            "  }"
            "}"
        )
    ) \
    .with_schema(  # declare the schema of the table
        Schema() 
            .field("windowStart", DataTypes.TIMESTAMP()).proctime()
            .field("windowEnd", DataTypes.TIMESTAMP()).proctime()
            .field("page", DataTypes.STRING())
            .field('count', DataTypes.BIGINT())
    ) \
    .in_append_mode() \
    .register_table_sink("ClickEventStatistics Sink")

# Tumble window aggregation
# 15 sec window
st_env.scan("ClickEvent Source") \
    .window(Tumble.over("15.seconds").on("timestamp").alias("w")) \
    .group_by("w, page") \
    .select("w.start as windowStart,w.end as windowEnd,page,count(1) as count") \
    .insert_into("ClickEventStatistics Sink")

# Now run it
st_env.execute("Click Event Count")
