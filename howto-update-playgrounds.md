
# Versioning 

When updating the playgrounds we have to deal with three versions that might need to be adjusted.

Externally defined versions:

* *Flink version*: Version of Apache Flink
* *Flink Docker image version*: Version of the official Flink Docker image on [Docker Hub](https://hub.docker.com/_/flink)

Internally defined version:

* *Playground Version*: This version is used for the Java artifacts and Docker images. It follows the scheme of `<Playground-Version>-Flink-<Minor-Flink-Version>`. For example `2-Flink-1.9` denotes Version 2 of the playground for Flink 1.9.x.

# Updating the playgrounds

## Update playgrounds due to a new bugfix Flink release

Apache Flink bugfix releases are frequently published. For example, Flink 1.8.2 is the second bugfix release in the Flink 1.8 release line. Bugfix releases are binary compatible with previous releases on the same minor release line.

When a new bugfix release is published, we have to wait until the corresponding Flink Docker image is published on [Docker Hub](https://hub.docker.com/_/flink). Once the Flink Docker image is available, update all playgrounds as follows:

1. All `pom.xml`: 
  * Update the versions of all Flink dependencies 
  * Update the Maven artifact version to the new playground version (`4-Flink-1.9` is the fourth update of the playground for the Flink 1.9 line).
  * All Maven projects should still build as bugfix releases should be compatible with previous versions.
2. All `Dockerfile`: 
	* Update the version of the base image to the new Flink Docker image version.
3. `docker-compose.yaml`: 
	* Update the version of the custom Docker images to the new playground version.
	* Update the version of the Flink containers to the new Flink docker image version if necessary.

The `flink-playgrounds` repository has a branch for every minor Flink release (for example `release-1.9` for all releases of the Flink 1.9 line). Updates for a bugfix release should be pushed to the existing branch of the updated release line.

## Update playgrounds due to a new minor (or major) Flink release

A major release marks a significant new version of Flink that probably breaks a lot of existing code. For example Flink 2.0.0 would be a major release that starts the Flink 2.x line. A minor release marks a new version of Apache Flink with significant improvements and changes. For example, Flink 1.9.0 is the minor release which starts the Flink 1.9 release line. New minor releases aim to be compatible but might deprecate, evolve, or remove code of previous releases. For updates due to minor and major Flink versions, the same update rules apply.

When a new minor or major release is published, we have to wait until the corresponding Flink Docker image is published on [Docker Hub](https://hub.docker.com/_/flink). Once the Flink Docker image is available, update all playgrounds as follows:

1. All `pom.xml`: 
  * Update the versions of all Flink dependencies 
  * Update the Maven artifact version to the new playground version (`1-Flink-1.9` is the first version of the playground for the Flink 1.9 line).
  * Check that all Maven projects still build and fix all issues related to the Flink version update.
2. All `Dockerfile`: 
	* Update the version of the base image to the new Flink Docker image version.
3. `docker-compose.yaml`: 
	* Update the version of the custom Docker images to the new playground version.
	* Update the version of the Flink containers to the new Flink docker image version.

The `flink-playgrounds` repository has a branch for every minor Flink release (for example `release-1.9` for all releases of the Flink 1.9 line). A playground update due to a new minor or major Flink release starts a new branch. Moreover, the new version should also be pushed to the `master` branch.

## Update a playground to improve a custom Docker image

You might want to update the software in a Docker image to add a new feature or fix a bug.
Whenever you update an image you have to increment the playground version in the following places.

* the artifact version of all Maven projects that are modified.
* the tag (name and version) of all affected custom images the `docker-compose.yaml` files.

The update of the `docker-compose.yaml` file is important to ensure that the updated Docker image gets a new version. 
Otherwise the new image might not be built if a user had run the playground before with an earlier image.

Changes to the playgrounds should be pushed to all affected branches (`release-x.z`, `master`) of the `flink-playgrounds` repository.

## Update a playground without updating a custom Docker image

If you want to use a different public (not custom) Docker image, you can just update the `docker-compose.yaml` file.

Changes to the playgrounds should be pushed to all affected branches (`release-x.z`, `master`) of the `flink-playgrounds` repository.