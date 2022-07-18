[//]: # (title: Custom engines)

<include src="lib.topic" element-id="outdated_warning"/>

Ktor's HTTP client and server provide a common interface while allowing to use of different engines to perform and handle HTTP requests.

Ktor includes several artifacts and engines:
* For the server: `Netty`, `Jetty`, `Tomcat`, `CIO`, `TestEngine`
* For the client: `ApacheEngine`, `JettyHttp2Engine`, `CIOEngine`, `TestHttpClientEngine`

## `ApplicationEngine` API

A simplified version of the `ApplicationEngine` looks like this:

<tabs>

```kotlin
interface ApplicationEngineFactory<
    out TEngine : ApplicationEngine,
    TConfiguration : ApplicationEngine.Configuration
> {
    fun create(environment: ApplicationEngineEnvironment, configure: TConfiguration.() -> Unit): TEngine
}

interface ApplicationEngine {
    val environment: ApplicationEngineEnvironment
    fun start(wait: Boolean = false): ApplicationEngine
    fun stop(gracePeriod: Long, timeout: Long, timeUnit: TimeUnit)

    open class Configuration {
        val parallelism: Int
        var connectionGroupSize: Int
        var workerGroupSize: Int
        var callGroupSize: Int
    }
}

interface ApplicationEngineEnvironment {
    val connectors: List<EngineConnectorConfig>
    val application: Application
    fun start()
    fun stop()

    val classLoader: ClassLoader
    val log: Logger
    val config: ApplicationConfig
    val monitor: ApplicationEvents
}

interface EngineConnectorConfig {
    val type: ConnectorType
    val host: String
    val port: Int
}

data class ConnectorType(val name: String) {
    companion object {
        val HTTP = ConnectorType("HTTP")
        val HTTPS = ConnectorType("HTTPS")
    }
}

abstract class BaseApplicationEngine(
    final override val environment: ApplicationEngineEnvironment,
    val pipeline: EnginePipeline = defaultEnginePipeline(environment)
) : ApplicationEngine {
    val application: Application
}
```

</tabs>

## `ApplicationEngineFactory`

Each implementation of the `ApplicationEngineFactory` along with a subtyped `ApplicationEngine.Configuration` define the publicly exposed APIs for each engine.

```kotlin
fun ApplicationEngineFactory.create(environment: ApplicationEngineEnvironment, configure: TConfiguration.() -> Unit): TEngine
```

The `ApplicationEngineFactory.create` instantiates the correct subtyped `ApplicationEngine.Configuration` and calls the provided `configure: TConfiguration.() -> Unit` lambda that should mutate the configuration object. It also constructs an implementation of the `ApplicationEngine`, most likely a subtype of `BaseApplicationEngine`.

For example:

```kotlin
class MyApplicationEngineFactory
    <MyApplicationEngine, MyApplicationEngineConfiguration>
{

    fun create(
        environment: ApplicationEngineEnvironment,
        configure: MyApplicationEngineConfiguration.() -> Unit
    ): MyApplicationEngine {
    
        val configuration = MyApplicationEngineConfiguration()
        configure(configuration)
        return MyApplicationEngine(environment, configuration)
    }
    
}
```

## `BaseApplicationEngine`

The interface `ApplicationEngine` with an abstract implementation of `BaseApplicationEngine` starts and stops the application.
It holds the `ApplicationEngineEnvironment` as well as the constructed configuration of the application.

This class has two methods:

* The `start` method: connects to the `ApplicationEngineEnvironment.connectors` (from the environment), starts the environment,
and starts and configures the engine to trigger execution of the `application` pipeline for each HTTP request with an `ApplicationCall`.
* The `stop` method: stops the engine and the environment, and unregisters all items registered by the `start` method.

The `BaseApplicationEngine` exposes an `ApplicationEngineEnvironment` passed to the constructor and creates an `EnginePipeline`,
which is used as an intermediary to pre-intercept the application pipeline. It also installs default transformations in the send and receive pipelines,
and logs the defined connection endpoints.

For example:

```kotlin
class MyApplicationEngine(
    environment: ApplicationEngineEnvironment,
    configuration: MyApplicationEngineConfiguration
) : BaseApplicationEngine(environment) {
    val myEngine = MyEngine()

    override fun start(wait: Boolean): MyApplicationEngine {
        environment.start()
        myEngine.start()
        myEngine.addRequestHandler(::handleRequest)
        return this
    }
    
    override fun stop(gracePeriod: Long, timeout: Long, timeUnit: TimeUnit) {
        myEngine.removeRequestHandler(::handleRequest)
        myEngine.stop()
        environment.stop()
    }
    
    private fun handleRequest(request: MyEngineCall) {
        val call: ApplicationCall = request.toApplicationCall()
        pipeline.execute(call)
    }
}

```