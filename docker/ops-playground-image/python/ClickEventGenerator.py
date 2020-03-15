
"""
ClickEventGenerator, Python version

This is a Python version of the java function as part of the 
flink-operations-playground, where the java code generates 1k timestamped
events simulating page clicks. See
* https://ci.apache.org/projects/flink/flink-docs-stable/getting-started/docker-playgrounds/flink-operations-playground.html
* https://github.com/fhueske/flink-intro-tutorial/wiki/flink-introduction

It needs to generate events like these at a rate of 1k events per second.
{"timestamp":"01-01-1970 01:10:53:160","page":"/index"}
{"timestamp":"01-01-1970 01:10:53:160","page":"/shop"}

We will use the package kafka-python to generate the kafka feed, see
https://kafka-python.readthedocs.io/en/stable/usage.html

"""
# Example adapted from kafka-python' official doc
# https://kafka-python.readthedocs.io/en/stable/usage.html

import os, json, random
from time import sleep
from datetime import datetime
from kafka import KafkaProducer
from kafka.errors import KafkaError

# the KAFKA_BOOTSTRAP_SERVERS env variable is passed to this 
# docker via docker-compose env vars.
# it's a string; it needs to be parsed as a list
try:
    # TODO: json.loads() may be a security risk
    KAFKA_BOOTSTRAP_SERVERS = json.loads(os.environ["KAFKA_BOOTSTRAP_SERVERS"])
except:
    # if any error, use this as default
    KAFKA_BOOTSTRAP_SERVERS = ['kafka:9092']

# KAFKA_TOPIC = "input" by default
try:
    KAFKA_TOPIC = os.environ["KAFKA_TOPIC"]
except KeyError:
    # if any error, use this as default
    KAFKA_TOPIC = "input"

# EventsPerSecond = 100
try:
    EVENTS_PER_SECOND = int(os.environ["EVENTS_PER_SECOND"])
except KeyError:
    # if any error, use this as default
    EVENTS_PER_SECOND = 100

# get Kafka producer
# we automatically encode JSON in utf-8
producer = KafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS, api_version=(0, 10, 1), 
                         value_serializer=lambda x: 
                         json.dumps(x).encode('utf-8'))

def on_send_success(record_metadata):
    # handle success
    pass

def on_send_error(excp):
    # log.error('I am an errback', exc_info=excp)
    # handle exception
    pass

# generate data and send to Kafka, at a rate of 1k per second
# TODO: the python time.sleep() is accurate to only 10ms on Windows, so this sleep()
#       strategy does not give you exactly 1000 events per second. 
pageNames=["/help", "/index", "/shop", "/jobs", "/about", "/news"]

while True:
    for _ in range(EVENTS_PER_SECOND):
        # note the trick to get millisonds versus microseconds; 
        # Flink wants msec, not microsec
        data = {
            "timestamp" : datetime.now().strftime("%d-%m-%Y %H:%M:%S:%f")[:-3],
            "page": random.choice(pageNames)
        }
        # producer.send(KAFKA_TOPIC, value=data)\
        #         .add_callback(on_send_success)\
        #         .add_errback(on_send_error)
        producer.send(KAFKA_TOPIC, value=data)
    sleep(1)


# block until all async messages are sent
producer.flush()

