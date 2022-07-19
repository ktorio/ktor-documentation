[//]: # (title: Docker)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="deployment-ktor-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to deploy a Ktor application to a Docker container, which can then be run either locally or on your cloud provider of choice.
</link-summary>

In this section, we'll see how to use the [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) for packaging, running, and deploying applications using [Docker](https://www.docker.com).



## Install the Ktor plugin {id="install-plugin"}

To install the Ktor plugin, add it to the `plugins` block of your `build.gradle.(kts)` file:

<tabs group="languages">
<tab title="Gradle (Kotlin)" group-key="kotlin">

```kotlin
plugins {
    id("io.ktor.plugin") version "%ktor_version%"
}
```
{interpolate-variables="true"}

</tab>
<tab title="Gradle (Groovy)" group-key="groovy">

```groovy
plugins {
    id "io.ktor.plugin" version "%ktor_version%"
}
```
{interpolate-variables="true"}

</tab>
</tabs>


## Plugin tasks {id="tasks"}

After [installing](#install-plugin) the plugin, the following tasks are available for packaging, running, and deploying applications:

- `buildImage`: builds a project's Docker image to a tarball. This task generates a `jib-image.tar` file in the `build` directory. You can load this image to a Docker daemon using the [docker load](https://docs.docker.com/engine/reference/commandline/load/) command:
   ```Bash
   docker load < build/jib-image.tar
   ```
- `publishImageToLocalRegistry`: builds and publishes a project's Docker image to a local registry.
- `runDocker`: builds a project's image to a Docker daemon and runs it. Executing this task will launch the Ktor server, responding on `https://0.0.0.0:8080` by default.
- `publishImage`: builds and publishes a project's Docker image to an external registry such as [Docker Hub](https://hub.docker.com/) or [Google Container Registry](https://cloud.google.com/container-registry). Note that you need to configure the external registry using the **[ktor.docker.externalRegistry](#external-registry)** property for this task.

Note that by default, these tasks build the image with the `ktor-docker-image` name and `latest` tag. 
You can customize these values in the [plugin configuration](#name-tag).

## Configure the Ktor plugin {id="configure-plugin"}

To configure the Ktor plugin settings related to Docker tasks, use the `ktor.docker` extension in your `build.gradle.(kts)` file:

```kotlin
ktor {
    docker {
        // ...
    }
}
```

### JRE version {id="jre-version"}

The `jreVersion` property specifies the JRE version to use in the image:

```kotlin
```
{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="29,34-35,46-47"}

### Image name and tag {id="name-tag"}

If you need to customize the image name and tag, use the `localImageName` and `imageTag` properties, respectively:

```kotlin
```
{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="29,34,36-37,46-47"}


### External registry {id="external-registry"}

Before publishing a project's Docker image to an external registry using the **[publishImage](#tasks)** task, you need to configure the external registry using the `ktor.docker.externalRegistry` property. This property accepts the `DockerImageRegistry` instance, which provides configuration for the required registry type:

- `DockerImageRegistry.dockerHub`: creates a `DockerImageRegistry` for [Docker Hub](https://hub.docker.com/).
- `DockerImageRegistry.googleContainerRegistry`: creates a `DockerImageRegistry` for [Google Container Registry](https://cloud.google.com/container-registry).

The example below shows how to configure the Docker Hub registry:

```kotlin
```
{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="29,34,39-47"}

Note that the Docker Hub name and password are fetched from the environment variables, so you need to set these values before running the `publishImage` task:

<tabs group="os">
<tab title="Linux/macOS" group-key="unix">

```Bash
export DOCKER_HUB_USERNAME=yourHubUsername
export DOCKER_HUB_PASSWORD=yourHubPassword
```

</tab>
<tab title="Windows" group-key="windows">

```Bash
setx DOCKER_HUB_USERNAME yourHubUsername
setx DOCKER_HUB_PASSWORD yourHubPassword
```

</tab>
</tabs>


## Manual image configuration {id="manual"}

If required, you can provide your own `Dockerfile` to assemble an image with a Ktor application.

### Package the application {id="packagea-pp"}
As a first step, you need to package your application along with its dependencies. 
For example, this might be a [fat JAR](fatjar.md) or an [executable JVM application](gradle-application-plugin.md).


### Prepare Docker image {id="prepare-docker"}

To dockerize the application, we'll use [multi-stage builds](https://docs.docker.com/develop/develop-images/multistage-build/):
- First, we'll use the `gradle`/`maven` image to generate a fat JAR with the application.
- Then, the generated distribution will be run in the environment created based on the `openjdk` image.

In the root folder of the project, create a file named `Dockerfile` with the following contents:

<tabs group="languages">
<tab title="Gradle" group-key="kotlin">

```dockerfile
FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]
```

</tab>
<tab title="Maven" group-key="maven">

```dockerfile
FROM maven:3-openjdk-11 AS build
COPY . /home/maven/src
WORKDIR /home/maven/src
RUN mvn package

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/maven/src/target/*-with-dependencies.jar /app/ktor-docker-sample.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-sample.jar"]
```

</tab>
</tabs>


The second stage of the build works in the following way:

* Indicates what image is going to be used (`openjdk` in this case).
* Specifies the exposed port (this does not automatically expose the port, which is done when running the container).
* Copies the contents from the build output to the folder.
* Runs the application (`ENTRYPOINT`).


You can find the resulting project in the **docker** branch of the [ktor-get-started-sample](https://github.com/ktorio/ktor-get-started-sample) repository.


### Build and run the Docker image {id="build-run"}

The next step is to build and tag the Docker image:

```bash
docker build -t my-application .
```

Finally, start the image:

```bash
docker run -p 8080:8080 my-application
```

This will launch the Ktor server, responding on `https://0.0.0.0:8080`.
