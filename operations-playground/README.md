# Flink Operations Playground

The Flink operations playground lets you explore and play with [Apache Flink](https://flink.apache.org)'s features to manage and operate stream processing jobs, including

* Observing automatic failure recovery of an application
* Upgrading and rescaling an application
* Querying the runtime metrics of an application

It's based on a [docker-compose](https://docs.docker.com/compose/) environment and is super easy to setup.

## Setup

The operations playground requires a custom Docker image, as well as public images for Flink, Kafka, and ZooKeeper. 

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

You can check if the playground was successfully started by accessing the WebUI of the Flink cluster at [http://localhost:8081](http://localhost:8081).

#### for Windows Users

If you get the error "Unhandled exception: Filesharing has been cancelled", you should configure file sharing in Docker Desktop before starting.
In Settings > Resources > File Sharing, add the operations-playground/conf directory.

### Stopping the Playground

To stop the playground, run the following command

```bash
docker-compose down
```

## Further instructions

The playground setup and more detailed instructions are presented in the
["Getting Started" guide](https://ci.apache.org/projects/flink/flink-docs-release-1.16/try-flink/flink-operations-playground.html) of Flink's documentation.
