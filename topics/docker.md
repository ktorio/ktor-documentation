[//]: # (title: Docker)

<microformat>
<var name="example_name" value="docker"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
Learn how to deploy a Ktor application to a Docker container, which can then be run either locally or on your cloud provider of choice.
</excerpt>

In this section we'll see how to deploy a Ktor application to a [Docker](https://www.docker.com) container, which can then be run either locally or on your cloud provider of choice.

Docker is a container system that allows for packaging software in a format that can then be run on any
platform that supports Docker, such as Linux, macOS, and Windows. Conceptually Docker is an operating system with
layers providing multiple services. While the basics of Docker will be covered, if you're not familiar with it, check out some of the 
[Getting Started](https://docs.docker.com/get-started/) documentation. 

## Get the application ready

In order to run on Docker, the application needs to have all the required files deployed to the container. As a first step,
you need to create a zip file containing the application and its dependencies. Depending on the build system you're using,
there are different ways to accomplish this. 

The example below will be using Gradle and the [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html) to accomplish this. If using Maven, the same thing
can be accomplished using the [assembly](http://maven.apache.org/guides/mini/guide-assemblies.html) functionality. 

### Configure the Gradle file


<tabs>

<tab title="Gradle">

```kotlin
```
{src="snippets/docker/build.gradle.kts"}

</tab>

<tab title="application.conf">

```groovy
```
{src="snippets/docker/src/main/resources/application.conf"}

</tab>

<tab title="Application.kt">

```kotlin
```
{src ="snippets/docker/src/main/kotlin/com/example/Application.kt"}

</tab>

</tabs>


## Prepare Docker image

In the root folder of the project create a file named `Dockerfile` with the following contents:

```dockerfile
```
{src="snippets/docker/Dockerfile"}


The Dockerfile indicates a few things:

* What image is going to be used (JDK 8 in this case).
* The exposed port (this does not automatically expose the port which is done when running the container)
* How to run the application (`cmd` file)

The other steps merely create a folder, copy the contents from the build output to the folder and change to it in preparation
to run the image.

## Build and run the Docker image

First step is to create the distribution of the application (in this case using Gradle):

```bash
./gradlew installDist
```

Next step is to build and tag the Docker image:

```bash
docker build -t my-application .
```

Finally, start the image:

```bash
docker run -p 8080:8080 my-application
```

For more information about running a docker image please consult [docker run](https://docs.docker.com/engine/reference/run) 
documentation.

If using [IntelliJ IDEA](https://www.jetbrains.com/idea), you can simply click `Run` in the Dockerfile
to perform these steps.

![Docker Run](run-docker.png){width="291"}
