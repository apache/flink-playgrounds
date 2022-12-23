# Apache Flink Playgrounds

This repository provides playgrounds to quickly and easily explore [Apache Flink](https://flink.apache.org)'s features.

The playgrounds are based on [docker-compose](https://docs.docker.com/compose/) environments.
Each subfolder of this repository contains the docker-compose setup of a playground, except for the `./docker` folder which contains code and configuration to build custom Docker images for the playgrounds.

## Available Playgrounds

Currently, the following playgrounds are available:

* The **Flink Operations Playground** (in the `operations-playground` folder) lets you explore and play with Flink's features to manage and operate stream processing jobs. You can witness how Flink recovers a job from a failure, upgrade and rescale a job, and query job metrics. The playground consists of a Flink cluster, a Kafka cluster and an example 
Flink job. The playground is presented in detail in
["Flink Operations Playground"](https://ci.apache.org/projects/flink/flink-docs-release-1.16/docs/try-flink/flink-operations-playground), which is part of the _Try Flink_ section of the Flink documentation.

* The **Table Walkthrough** (in the `table-walkthrough` folder) shows how to use the Table API to build an analytics pipeline that reads streaming data from Kafka and writes results to MySQL, along with a real-time dashboard in Grafana. The walkthrough is presented in detail in ["Real Time Reporting with the Table API"](https://ci.apache.org/projects/flink/flink-docs-release-1.16/docs/try-flink/table_api), which is part of the _Try Flink_ section of the Flink documentation.

* The **PyFlink Walkthrough** (in the `pyflink-walkthrough` folder) provides a complete example that uses the Python API, and guides you through the steps needed to run and manage Pyflink Jobs. The pipeline used in this walkthrough reads data from Kafka, performs aggregations, and writes results to Elasticsearch that are visualized with Kibana. This walkthrough is presented in detail in the [pyflink-walkthrough README](pyflink-walkthrough).

## About

Apache Flink is an open source project of The Apache Software Foundation (ASF).

Flink is distributed data processing framework with powerful stream and batch processing capabilities.
Learn more about Flink at [https://flink.apache.org/](https://flink.apache.org/)
