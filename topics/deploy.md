[//]: # (title: Deployment)


In this topic, we'll make an overview of how to deploy a Ktor application.

## Ktor deployment specifics {id="ktor-specifics"}
The deployment process for a server Ktor application depends on the following specifics:
* Whether you are going to deploy your application as a self-contained package or inside a servlet container.
* Which approach do you use to create and configure a server.

### Self-contained app vs Servlet container {id="self-contained-vs-servlet"}

Ktor allows you to create and start a server with the desired network [engine](Engines.md) (such as Netty, Jetty, or Tomcat) right in the application. In this case, an engine is a part of your application. Your application has control over engine settings, connection, and SSL options. To deploy your application, you can [package](#packaging) it as a FatJAR or an executable JVM application.

In contrast to the approach above, a servlet container should control the application lifecycle and connection settings. Ktor provides a special `ServletApplicationEngine` engine that delegates control over your application to a servlet container. To deploy inside a servlet container, you need to generate a [WAR archive](war.md).

### Configuration in code vs HOCON file {id="code-hocon"}

Configuring a Ktor application for deployment might depend on the approach used to [create and configure a server](create_server.xml): in code or by using the `application.conf` configuration file. As an example, a [hosting provider](#publishing) may require to specify a port used to listen for incoming requests. In this case, you need to [configure](Configurations.xml) a port either in code or in the `application.conf`.


## Packaging {id="packaging"}

Before deploying your application, you need to package it in one of the following ways:

* Fat JAR

  A fat JAR is an executable JAR that includes all code dependencies. You can deploy it to any [cloud service](#publishing) that supports fat JARs. A fat JAR is also required if you need to generate a native binary for GraalVM. To create a fat JAR, you can use the [Shadow](fatjar.md) plugin for Gradle or the [Assembly](maven-assembly-plugin.md) plugin for Maven.

* Executable JVM application

   An executable JVM application is a packaged application that includes code dependencies and generated start scripts. As an example, you can use it to run a Ktor application inside [Docker](docker.md).

* WAR

   A [WAR archive](war.md) lets you deploy your application inside a servlet container, such as Tomcat or Jetty.

* GraalVM

   Ktor Server applications can make use of [GraalVM](Graalvm.md) in order to have native images for different platforms.



## Containerizing {id="containerizing"}

After you package your application (for example, to an executable JVM application or a fat JAR), you can prepare a [Docker image](docker.md) with this application. This image can then be used to run your application on Kubernetes, Swarm, or a required cloud service container instance.

## Publishing {id="publishing"}

Tutorials below show how to deploy a Ktor application to specific cloud providers:
* [](google-app-engine.md)
* [](heroku.md)
* [](elastic-beanstalk.md)

## SSL {id="ssl"}

If your Ktor server is placed behind a reverse proxy (such as Nginx or Apache) or runs inside a servlet container (Tomcat or Jetty), SSL settings are managed by a reverse proxy or a servlet container. If required, you can configure Ktor to serve [SSL directly](ssl.md) by using Java KeyStore.

> Note that SSL settings are not in effect when a Ktor application is deployed inside a servlet container.



