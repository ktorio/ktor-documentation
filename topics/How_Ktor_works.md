[//]: # (title: How Ktor works)

<include src="lib.md" include-id="outdated_warning"/>

Ktor is designed to be flexible and extensible. It is composed
of small, simple pieces, but if you don't know what's happening, then it is like a black box.

In this section, you will discover what Ktor is doing under the hood, and you will learn more
about its generic infrastructure.





## Entry points

You can run a Ktor application in several ways:

* With a plain `main` by calling `embeddedServer`
* Running a `EngineMain` `main` function and [using a HOCON `application.conf` configuration file](Configurations.md)
* [As a Servlet within a web server](https://github.com/ktorio/ktor-samples/tree/1.3.0/deployment)
* As part of a test using `withTestApplication` from the [`ktor-server-test-host`](https://github.com/ktorio/ktor/tree/main/ktor-server/ktor-server-test-host) artifact

## Start-up

### Common

**[`ApplicationEngineEnvironment`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-host-common/jvm/src/io/ktor/server/engine/ApplicationEngineEnvironment.kt):**

To begin with, this immutable environment has to be built;
with a classLoader, a logger, a [configuration](Configurations.md),
a monitor that acts as an event bus for application events,
and a set of connectors and modules, that will form the application and [watchPaths](Auto_reload.xml).

You can build it using `ApplicationEngineEnvironmentBuilder`,
and handy DSL functions `applicationEngineEnvironment`, `commandLineEnvironment` among others.

**[`ApplicationEngine`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-host-common/jvm/src/io/ktor/server/engine/ApplicationEngine.kt):**

There are multiple `ApplicationEngine`, one per supported server like:
Netty, Jetty, CIO or Tomcat.

The application engine is the class in charge of running the application,
it has a specific **configuration**, an associated **environment** and can be `start`ed and `stop`ped.

When you start a specific application engine, it will use the configuration
provided to listen, to the right ports and hosts,
by using SSL, certificates and so on, with the specified workers.

Connectors will be used for listening to specific http/https hosts and ports.
While the `Application` pipeline will be used to handle the requests.

**[Application](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/application/Application.kt) Pipeline**:

It is created by the `ApplicationEngineEnvironment` and it is initially empty.
It is a pipeline without a subject that has `ApplicationCall` as the context.
Each specified module will be called to configure this application when the
environment is created.

### embeddedServer

When you run your own main method and call the `embeddedServer` function,
you provide a specific `ApplicationEngineFactory` and
an `ApplicationEngineEnvironment` is then created or provided.

### EngineMain

Ktor defines one `EngineMain` class per each supported server engine.
This class defines a `main` method that can be executed to run the application.
By using `commandLineEnvironment` it will load the [HOCON `application.conf`](Configurations.md)
file from your resources and will use extra arguments to determine which modules to install
and how to configure the server.

Those classes are normally declared in `CommandLine.kt` source files.

* CIO: [`io.ktor.server.cio.EngineMain.main`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-cio/jvm/src/io/ktor/server/cio/EngineMain.kt)
* Jetty: [`io.ktor.server.jetty.EngineMain.main`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-jetty/jvm/src/io/ktor/server/jetty/EngineMain.kt)
* Netty: [`io.ktor.server.netty.EngineMain.main`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-netty/jvm/src/io/ktor/server/netty/EngineMain.kt)
* Tomcat: [`io.ktor.server.tomcat.EngineMain.main`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-tomcat/jvm/src/io/ktor/server/tomcat/EngineMain.kt)

### [TestApplicationEngine](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-test-host/jvm/src/io/ktor/server/testing/TestApplicationEngine.kt)

For testing, Ktor defines a `TestApplicationEngine` and `withTestApplication` handy method,
that will allow you to test your application modules, pipeline, and other features without
actually starting any server or mocking any facility.
It will use an in-memory configuration `MapApplicationConfig("ktor.deployment.environment" to "test")`
that you can use to determine if it is to run in a test environment.

## Monitor events

Associated with the environment is a monitor instance that Ktor uses to raise application events.
You can use it to subscribe to events. For example, you can subscribe to a stop application event
to shutdown specific services or finalize some resources.

A list of [Ktor defined events](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/application/ApplicationEvents.kt):

```kotlin
val ApplicationStarting = EventDefinition<Application>()
val ApplicationStarted = EventDefinition<Application>()
val ApplicationStopPreparing = EventDefinition<ApplicationEnvironment>()
val ApplicationStopping = EventDefinition<Application>()
val ApplicationStopped = EventDefinition<Application>()
```

## [Pipelines](https://github.com/ktorio/ktor/blob/main/ktor-utils/common/src/io/ktor/util/pipeline/Pipeline.kt)

Ktor defines pipelines for asynchronous extensible computations. Pipelines are used all over Ktor.

All the pipelines have an associated **subject** type, **context** type, and a list of **phases**
with **interceptors** associated to them. As well as, **attributes** that act as a small typed object container.

Phases are ordered and can be defined to be executed, after or before another phase, or at the end.

Each pipeline has an ordered list of phase contexts for that instance, which contain a set of
interceptors for each phase.

For example:

* Pipeline
    * Phase1
        * Interceptor1
        * Interceptor2
    * Phase2
        * Interceptor3
        * Interceptor4

The idea here is that each interceptor for a specific phase does not depend on other interceptors
on the same phase, but on interceptors from previous phases.

When executing a pipeline, all the registered interceptors will be executed in the order defined by the phases.

### [ApplicationCallPipeline](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/application/ApplicationCallPipeline.kt)

The server part of Ktor defines an `ApplicationCallPipeline` without a subject
and with `ApplicationCall` as context.
The `Application` instance is an `ApplicationCallPipeline`.

So when the server's application engine handles a HTTP request, it will execute the `Application`
pipeline.

The context class `ApplicationCall` contains the application, the `request`, the `response`,
and the `attributes` and `parameters`.

In the end, the application modules, will end registering interceptors
for specific phases for the Application pipeline, to process the `request` and emitting a `response`.

The `ApplicationCallPipeline` defines the following built-in phases for its pipeline:

```kotlin
val Setup = PipelinePhase("Setup") // Phase for preparing the call, and processing attributes
val Monitoring = PipelinePhase("Monitoring") // Phase for tracing calls: logging, metrics, error handling etc. 
val Features = PipelinePhase("Features") // Phase for infrastructure features, most intercept at this phase
val Call = PipelinePhase("Call") // Phase for processing a call and sending a response
val Fallback = PipelinePhase("Fallback") // Phase for handling unprocessed calls
```

## [Features](Plugins.md)

Ktor defines application features using the [`ApplicationFeature`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/application/ApplicationFeature.kt) class.
A feature is something that you can `install` to a specific pipeline.
It has access to the pipeline, and it can register interceptors and do all sorts of other things.

## Routing

To illustrate how features and a pipeline tree work together, let's have a look at how [Routing](Routing_in_Ktor.md) works.

Routing, like other features, is normally installed like this:

```kotlin
install(Routing) { }
```

But there is an easy method to register and start using it, that also installs it if it is not already registered:

```kotlin
routing { }
```

Routing is defined as a tree, where each node is a `Route` that is also a separate instance of an `ApplicationCallPipeline`.
So when the root routing node is executed, it will execute its own pipeline. And will stop executing things once
the route has been processed.

## What's next

- [Application calls](calls.md)
- [Application configuration](Configurations.md)
- [Pipelines exlained](Pipelines.md)





