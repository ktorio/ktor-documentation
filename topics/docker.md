[//]: # (title: Docker)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="deployment-ktor-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<web-summary>
Learn how to deploy a Ktor application to a Docker container, which can then be run either locally or on your cloud provider of choice.
</web-summary>

<link-summary>
Learn how to deploy your application to a Docker container.
</link-summary>

In this section, we'll see how to use the [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) for
packaging, running, and deploying applications using [Docker](https://www.docker.com).

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

> The Docker integration is automatically disabled if you apply the Ktor Gradle plugin along with the Kotlin Multiplatform Gradle plugin.
> To use this feature with KMP, you'll need to move the Ktor plugin to a separate project:
> 1. Create a JVM-only project with the Ktor Gradle plugin applied as shown above.
> 2. Add the Kotlin Multiplatform project as a dependency to that JVM-only project.
>
> If in your case it is necessary to apply both Gradle plugins in the same project,
> let us know by leaving a comment in [KTOR-8464](https://youtrack.jetbrains.com/issue/KTOR-8464).
>
{style="warning"}

## Plugin tasks {id="tasks"}

After [installing](#install-plugin) the plugin, the following tasks are available for packaging, running, and deploying
applications:

- `buildImage`: builds a project's Docker image to a tarball. This task generates a `jib-image.tar` file in the `build`
  directory. You can load this image to a Docker daemon using
  the [docker load](https://docs.docker.com/engine/reference/commandline/load/) command:
   ```Bash
   docker load < build/jib-image.tar
   ```
- `publishImageToLocalRegistry`: builds and publishes a project's Docker image to a local registry.
- `runDocker`: builds a project's image to a Docker daemon and runs it. Executing this task will launch the Ktor server,
  responding on `http://0.0.0.0:8080` by default. If your server is configured to use another port, you can
  adjust [port mapping](#port-mapping).
- `publishImage`: builds and publishes a project's Docker image to an external registry such
  as [Docker Hub](https://hub.docker.com/) or [Google Container Registry](https://cloud.google.com/container-registry).
  Note that you need to configure the external registry using the **[ktor.docker.externalRegistry](#external-registry)**
  property for this task.

Note that by default, these tasks build the image with the `ktor-docker-image` name and `latest` tag.
You can customize these values in the [plugin configuration](#name-tag).

## Configure the Ktor plugin {id="configure-plugin"}

To configure the Ktor plugin settings related to Docker tasks, use the `ktor.docker` extension in
your `build.gradle.(kts)` file:

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

{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="28,33-34,52-53"}

### Image name and tag {id="name-tag"}

If you need to customize the image name and tag, use the `localImageName` and `imageTag` properties, respectively:

```kotlin
```

{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="28,33,35-36,52-53"}

### Port mapping {id="port-mapping"}

By default, the [runDocker](#tasks) task publishes the `8080` container port to the `8080` Docker host port.
If required, you can change these ports using the `portMappings` property.
This might be useful if your server is [configured](server-configuration-file.topic#predefined-properties) to use
another port.

The example below shows how to map the `8080` container port to the `80` Docker host port.

```kotlin
```

{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="28,33,37-43,52-53"}

In this case, you can access the server on `http://0.0.0.0:80`.

### External registry {id="external-registry"}

Before publishing a project's Docker image to an external registry using the **[publishImage](#tasks)** task, you need
to configure the external registry using the `ktor.docker.externalRegistry` property. This property accepts
the `DockerImageRegistry` instance, which provides configuration for the required registry type:

- `DockerImageRegistry.dockerHub`: creates a `DockerImageRegistry` for [Docker Hub](https://hub.docker.com/).
- `DockerImageRegistry.googleContainerRegistry`: creates a `DockerImageRegistry`
  for [Google Container Registry](https://cloud.google.com/container-registry).

The example below shows how to configure the Docker Hub registry:

```kotlin
```

{src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="28,33,45-53"}

Note that the Docker Hub name and password are fetched from the environment variables, so you need to set these values
before running the `publishImage` task:

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
For example, this might be a [fat JAR](server-fatjar.md) or an [executable JVM application](server-packaging.md).

### Prepare Docker image {id="prepare-docker"}

To dockerize the application, we'll
use [multi-stage builds](https://docs.docker.com/develop/develop-images/multistage-build/):

1. First, we'll set up caching for Gradle/Maven dependencies. This step is optional, but recommended as it improves the
   overall build speed.
2. Then, we'll use the `gradle`/`maven` image to generate a fat JAR with the application.
3. Finally, the generated distribution will be run in the environment created based on the JDK image.

In the root folder of the project, create a file named `Dockerfile` with the following contents:

<tabs group="languages">
<tab title="Gradle" group-key="kotlin">

<code-block lang="Docker" src="snippets/tutorial-server-docker-compose/Dockerfile"/>

</tab>
<tab title="Maven" group-key="maven">

```Docker
# Stage 1: Cache Maven dependencies
FROM maven:3.8-amazoncorretto-21 AS cache
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Stage 2: Build Application
FROM maven:3.8-amazoncorretto-21 AS build
WORKDIR /app
COPY --from=cache /root/.m2 /root/.m2
COPY . .
RUN mvn clean package

# Stage 3: Create the Runtime Image
FROM amazoncorretto:21-slim AS runtime
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/target/*-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

</tab>
</tabs>

The first stage ensures dependencies will be re-downloaded only when there is a change to the
build related files. If the first stage is not used, or dependencies are not cached in other stages,
dependencies will be installed in every build.

In the second stage the fat JAR is built. Note that Gradle also supports shadow and boot JAR by default.

The third stage of the build works in the following way:

* Indicates what image is going to be used.
* Specifies the exposed port (this does not automatically expose the port, which is done when running the container).
* Copies the contents from the build output to the folder.
* Runs the application (`ENTRYPOINT`).

<tip id="jdk_image_replacement_tip">
  <p>
   This example uses the Amazon Corretto Docker Image, but you can substitute it with any other suitable alternative, such as the following:
  </p>
  <list>
    <li><a href="https://hub.docker.com/_/eclipse-temurin">Eclipse Temurin</a></li>
    <li><a href="https://hub.docker.com/_/ibm-semeru-runtimes">IBM Semeru</a></li>
    <li><a href="https://hub.docker.com/_/ibmjava">IBM Java</a></li>
    <li><a href="https://hub.docker.com/_/sapmachine">SAP Machine JDK</a></li>
  </list>
</tip>

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
