# pyflink-walkthrough

## Background

In this playground, you will learn how to manage and run PyFlink Jobs. The pipeline of this walkthrough reads data from Kafka, performs aggregations and writes results to Elasticsearch visualized via Kibana. The environment is managed by Docker so that all you need is a docker on your computer.

- Kafka

Kafka is used to store input data in this walkthrough. The script [generate_source_data.py](https://github.com/hequn8128/pyflink-walkthrough/blob/master/generate_source_data.py) is used to generate transaction data and writes into the payment_msg kafka topic. Each record includes 5 fields: 
```text
{"createTime": "2020-08-12 06:29:02", "orderId": 1597213797, "payAmount": 28306.44976403719, "payPlatform": 0, "provinceId": 4}
```
```text
createTime: The creation time of the transaction. 
orderId: The id of the current transaction.
payAmount: The pay amount of the current transaction.
payPlatform: The platform used to pay the order, pc or mobile.
provinceId: The id of the province for the user. 
```

- Generator 

A simple data generator is provided that continuously writes new records into Kafka. 
You can use the following command to read data in kafka and check whether the data is generated correctly.

```shell script
$ docker-compose exec kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic payment_msg
{"createTime":"2020-07-27 09:25:32.77","orderId":1595841867217,"payAmount":7732.44,"payPlatform":0,"provinceId":3}
{"createTime":"2020-07-27 09:25:33.231","orderId":1595841867218,"payAmount":75774.05,"payPlatform":0,"provinceId":3}
{"createTime":"2020-07-27 09:25:33.72","orderId":1595841867219,"payAmount":65908.55,"payPlatform":0,"provinceId":0}
{"createTime":"2020-07-27 09:25:34.216","orderId":1595841867220,"payAmount":15341.11,"payPlatform":0,"provinceId":1}
{"createTime":"2020-07-27 09:25:34.698","orderId":1595841867221,"payAmount":37504.42,"payPlatform":0,"provinceId":0}
```

- PyFlink

The transaction data is processed by a PyFlink job, [payment_msg_proccessing.py](https://github.com/hequn8128/pyflink-walkthrough/blob/master/payment_msg_proccessing.py). The job maps the province id to province name for better demonstration using a Python UDF and then sums the payment for each province using a group aggregate. 

- ElasticSearch

ElasticSearch is used to store upstream processing results and provide efficient query service.

- Kibana

Kibana is an open source data visualization dashboard for ElasticSearch. We use it to visualize our processing results.

## Setup

The pyflink-walkthrough requires a custom Docker image, as well as public images for Flink, Elasticsearch, Kafka, and ZooKeeper. 

The [docker-compose.yaml](https://github.com/hequn8128/pyflink-walkthrough/blob/master/docker-compose.yml) file of the pyflink-walkthrough is located in the `pyflink-walkthrough` root directory.

### Building the custom Docker image

Build the Docker image by running

```bash
docker-compose build
```

### Starting the Playground

Once you built the Docker image, run the following command to start the playground

```bash
docker-compose up -d
```

You can check if the playground was successfully started by accessing the WebUI of(You may need to wait about 1 min before all services come up.):

1. visiting Flink Web UI [http://localhost:8081](http://localhost:8081).
2. visiting Elasticsearch [http://localhost:9200](http://localhost:9200).
3. visiting Kibana [http://localhost:5601](http://localhost:5601).


### Stopping the Playground

To stop the playground, run the following command

```bash
docker-compose down
```


## Run jobs

1. Submit the PyFlink job.
```shell script
$ docker-compose exec jobmanager ./bin/flink run -py /opt/pyflink-walkthrough/payment_msg_proccessing.py -d
```

2. Open [kibana ui](http://localhost:5601) and choose the dashboard: payment_dashboard

![image](pic/dash_board.png)

![image](pic/final.png)

3. Stop PyFlink job:

Visit [http://localhost:8081/#/overview](http://localhost:8081/#/overview) , select the job and click `Cancle`.

![image](pic/cancel.png)
