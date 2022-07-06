[//]: # (title: Docker)

<tldr>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-get-started-sample">ktor-get-started-sample</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-get-started-sample/tree/docker">ktor-get-started-sample</a>, the <control>docker</control> branch
</p>
</tldr>

<link-summary>
Learn how to deploy a Ktor application to a Docker container, which can then be run either locally or on your cloud provider of choice.
</link-summary>

In this section, we'll see how to deploy a Ktor application to a [Docker](https://www.docker.com) container, which can then be run either locally or on your cloud provider of choice.

Docker is a container system that allows for packaging software in a format that can then be run on any
platform that supports Docker, such as Linux, macOS, and Windows. Conceptually Docker is an operating system with
layers providing multiple services. While the basics of Docker will be covered, if you're not familiar with it, check out the [Getting Started](https://docs.docker.com/get-started/) documentation. 

## Clone a sample application {id="clone"}
In this tutorial, we'll be using a project created in [](intellij-idea.xml): [ktor-get-started-sample](https://github.com/ktorio/ktor-get-started-sample).


## Get the application ready {id="prepare-app"}
In order to run on Docker, the application needs to have all the required files deployed to the container. As a first step,
you need to create a fat JAR file containing the application and its dependencies. Depending on the build system you're using,
there are different ways to accomplish this:
- To prepare a Gradle project, follow the steps from the [](fatjar.md#configure-plugin) section.
- To prepare a Maven project, follow the steps from [](maven-assembly-plugin.md#configure-plugin).



## Prepare Docker image {id="prepare-docker"}

To dockerize the application, we'll use [multi-stage builds](https://docs.docker.com/develop/develop-images/multistage-build/):
- First, we'll use the `gradle`/`maven` image to generate a distribution of the application.
- Then, the generated distribution will be run in the environment created based on the `openjdk` image.

In the root folder of the project, create a file named `Dockerfile` with the following contents:

<tabs group="languages">
<tab title="Gradle" group-key="kotlin">

```dockerfile
FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

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


## Build and run the Docker image {id="build-run"}

The next step is to build and tag the Docker image:

```bash
docker build -t my-application .
```

Finally, start the image:

```bash
docker run -p 8080:8080 my-application
```

If using IntelliJ IDEA, you can click `Run` in the `Dockerfile` to perform these steps:

![Docker Run](run-docker.png){width="291"}

Learn more from the [Docker](https://www.jetbrains.com/help/idea/docker.html) topic.
