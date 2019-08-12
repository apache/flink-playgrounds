# Apache Flink Playgrounds

This repository provides playgrounds to quickly and easily explore [Apache Flink](https://flink.apache.org)'s features.

The playgrounds based on [docker-compose](https://docs.docker.com/compose/) environments.
The docker-compose configuration files of the playgrounds are located in the subfolders of the repository, except for the `./docker` folder which contains code and configuration to build Docker images.

## Available Playgrounds

Currently, the following playgrounds are available:

* The **Flink Operations Playground** in the (`operations-playground` folder) let's you explore and play with Flink's features to manage and operate stream processing jobs. You can witness how Flink recovers a job from a failure, upgrade and rescale a job, and query job metrics. The playground consists of a Flink cluster, a Kafka cluster and an example 
Flink job. The playground is presented in detail in the
["Getting Started" guide](https://ci.apache.org/projects/flink/flink-docs-release-1.8/getting-started/docker-playgrounds/flink-cluster-playground.html) of Flink's documentation.

* The interactive SQL playground is still under development and will be added shortly.

## About

Apache Flink is an open source project of The Apache Software Foundation (ASF).

Flink is distributed data processing framework with powerful stream and batch processing capabilities.
Learn more about Flink at [http://flink.apache.org/](https://flink.apache.org/)