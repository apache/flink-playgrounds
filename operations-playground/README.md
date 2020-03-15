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

### Stopping the Playground

To stop the playground, run the following command

```bash
docker-compose down
```

## Further instructions

The playground setup and more detailed instructions are presented in the
["Getting Started" guide](https://ci.apache.org/projects/flink/flink-docs-release-1.10/getting-started/docker-playgrounds/flink-operations-playground.html) of Flink's documentation.

----
## Python ClickEvent Example

The `./docker/python` folder contains a pyFlink example of the Click Event Counter example. It is similar to the Java example in the Playground with the following differences:
- It is implemented in python using `pyFlink` and `kafka-python` libraries
- It sents about 1k events, randomized across the 6 click event types; this is different from the deterministic behavior of the java example, where it's 1k events per window per event type. 

To run:

```
docker-compose -f pyflink_docker-compose.yaml up -d
```

Then open the browser to [http://localhost:8081](http://localhost:8081) to see that this job is complete. To see the output, one needs to log on to the `taskmanager` container while the `docker-compose` is still up. 

To check output 

```
docker-compose exec kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic output
```

To shut down the docker-compose example, 
```
docker-compose down
```


----
## Python Batch Example

The `./docker/python` folder contains a pyFlink example from https://ci.apache.org/projects/flink/flink-docs-master/getting-started/walkthroughs/python_table_api.html

- the [`WordCount.py`](../docker/ops-playground-image/python/WordCount.py) file contains the source from the above page
- the [`input.txt`](../docker/ops-playground-image/python/input.txt) file is the input text file, to be mapped to `input` in the docker image (Windows doesn't like files without extention)
- [`Dockerfile`](../docker/ops-playground-image/Dockerfile) is modified to install `python` and `pyFlink`
- [`pyflink_docker-compose.yaml`](pyflink_docker-compose.yaml) is created for this example. 

To run this example, on your host computer, start a terminal and change directory to where you have `pyflink_docker-compose.yaml`, then

```
docker-compose -f pyflink_docker-compose.yaml up -d
```

Then open the browser to [http://localhost:8081](http://localhost:8081) to see that this job is complete. To see the output, one needs to log on to the `taskmanager` container while the `docker-compose` is still up. 

```
docker-compose -f pyflink_docker-compose.yaml exec taskmanager /bin/bash
```
Then
```
more /tmp/output
```
To exit the docker commandline interface, type `exit`

To shut down the docker-compose example, 
```
docker-compose down
```
