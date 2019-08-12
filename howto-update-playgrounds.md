
# Versioning 

When updating the playgrounds we have to deal with three versions that need to be adjusted.

Externally defined versions:

* *Flink version*: Version of Apache Flink
* *Flink Docker image version*: Version of the official Flink Docker image on [Docker Hub](https://hub.docker.com/_/flink)

Internally defined version:

* *Playground Version*: This version is used for the Java artifacts and Docker images. It follows the scheme of `<Playground-Version>-Flink-<Minor-Flink-Version>`. For example `2-Flink-1.9` denotes Version 2 of the playground for Flink 1.9.x.

# Updating the playgrounds

## Update playgrounds due to a new minor (or major) Flink release

First of all, check that a Flink Docker image was published on [Docker Hub](https://hub.docker.com/_/flink) for the new Flink version.

Update all playgrounds as follows:

1. All `pom.xml`: 
  * Update the versions of all Flink dependencies 
  * Update the Maven artifact version to the new playground version (`1-Flink-1.9` if 1.9.0 is the new Flink release).
  * Check that all Maven projects build.
2. All `Dockerfile`: 
	* Update version of the base image to the new Flink Docker image version.
3. `docker-compose.yaml`: 
	* Update the version of the Flink containers to the new Flink docker image version.
	* Update the version of the custom Docker images to the new playground version.

## Update a playground due to bug in a custom Docker image

Whenever, you need to update a Docker image, please increment the playground version in the following places.

* the artifact version of all Maven projects in all `pom.xml` files.
* the tag (name and version) of all custom images in all `docker-compose.yaml` files.

## Update a playground without updating a custom Docker image

Just update the `docker-compose.yaml` file.
