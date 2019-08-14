# Flink Operations Playground

The Flink operations playground let's you explore and play with [Apache Flink](https://flink.apache.org)'s features to manage and operate stream processing jobs, including

* Observing automatic failure recovery of an application
* Upgrading and rescaling an application
* Querying the runtime metrics of an application

It is based on a [docker-compose](https://docs.docker.com/compose/) environment and super easy to setup.

## Setup

The operations playground requires a custom Docker image in addition to public images for Flink, Kafka, and ZooKeeper. 

The `docker-compose.yaml` file of the operations playground is located in the `operations-playground` directory. Assuming you are at the root directory of the [`flink-playgrounds`](https://github.com/apache/flink-playgrounds) repository, change to the `operations-playground` folder by running

```bash
cd operations-playground
```

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

You can check if the playground was successfully started, if you can access the WebUI of the Flink cluster at [http://localhost:8081](http://localhost:8081).

### Stopping the Playground

To stop the playground, run the following command

```bash
docker-compose down
```

## Further instructions

The playground setup and more detailed instructions are presented in the
["Getting Started" guide](https://ci.apache.org/projects/flink/flink-docs-release-1.8/getting-started/docker-playgrounds/flink-cluster-playground.html) of Flink's documentation.