[//]: # (title: Configurations)

<include src="lib.xml" include-id="outdated_warning"/>

Ktor uses [HOCON (Human-Optimized Config Object Notation)](https://github.com/lightbend/config/blob/main/HOCON.md)
format in the external configuration file. In this file, you can configure things like the port to listen to,
or the [modules](Modules.md) to be loaded. This format is similar to JSON,
but is optimized to be read and written by humans, and supports additional
features like environment variable substitution.
In this case, you configure the server engine to be used with the `mainClassName` pointing to a particular `EngineMain`.

Ktor also uses a set of lambdas with a typed DSL (Domain Specific Language) to configure the application
and server engine when using `embeddedServer`.

>Starting with Ktor 1.0.0-beta-2, the `DevelopmentEngine` class has been renamed to `EngineMain`, for older versions just rename it.
>
{type="note"}

## The HOCON file
{id="hocon-file"}

This is the preferred way of configuring Ktor applications as it allows you
to easily change the configuration without recompiling your application.

When Ktor is started using a `EngineMain`, or by calling the `commandLineEnvironment`,
it tries to load a HOCON file called `application.conf` from the application resources.
You can change the location of the file using [command line arguments](#command-line).

Available development engines that you can use as `mainClassName`:

* `io.ktor.server.cio.EngineMain`
* `io.ktor.server.tomcat.EngineMain`
* `io.ktor.server.jetty.EngineMain`
* `io.ktor.server.netty.EngineMain`


Ktor only requires you to specify which [module or modules](Modules.md)
you want it to load when starting the server using the `ktor.application.modules` property.
All the other properties are optional.

A typical, simple HOCON file for Ktor (`application.conf`) would look like this:

```kotlin
ktor {
    deployment {
        port = 8080
    }

    application {
        modules = [ io.ktor.samples.metrics.MetricsApplicationKt.main ]
    }
}
```

Using dot notation it would be equivalent to:

```kotlin
ktor.deployment.port = 8080
ktor.application.modules = [ io.ktor.samples.metrics.MetricsApplicationKt.main ]
```

Ktor allows you to configure much more: from additional core configurations
to Ktor features, and even custom configurations for your applications:

```kotlin
ktor {
    deployment {
        environment = development
        port = 8080
        sslPort = 8443
        autoreload = true
        watch = [ httpbin ]
    }

    application {
        modules = [ io.ktor.samples.httpbin.HttpBinApplicationKt.main ]
    }

    security {
        ssl {
            keyStore = build/temporary.jks
            keyAlias = mykey
            keyStorePassword = changeit
            privateKeyPassword = changeit
        }
    }
}

jwt {
    domain = "https://jwt-provider-domain/"
    audience = "jwt-audience"
    realm = "ktor sample app"
}

youkube {
  session {
    cookie {
      key = 03e156f6058a13813816065
    }
  }
  upload {
    dir = ktor-samples/ktor-samples-youkube/.video
  }
}
```

There is a [list of the available core configurations](#available-config) in this document.

>You can use HOCON to [set properties from environment variables](https://github.com/lightbend/config/blob/main/HOCON.md#substitutions).
>
{type="note"}

>There is an [IntelliJ plugin for HOCON](https://plugins.jetbrains.com/plugin/10481-hocon), that you may want to install.
>
{type="note"}

## Command Line
{id="command-line"}

When using [`commandLineEnvironment`](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-host-common/src/io/ktor/server/engine/CommandLine.kt)
(any `EngineMain` main) there are several switches and configuration parameters you can use to configure
your application module.

If you start the application from the command line with `-config=anotherfile.conf`, it will
load the config file from the specific local file instead of from the resources.

Using switches, you can, for example, override the bound port defined by executing:

`java -jar myapp-fatjar.jar -port=8080`

There is a [list of available command line switches](#available-config) in this document.

## Configuring the embeddedServer
{id="embedded-server"}

### Multiple connectors

It is possible to define by code several connectors using the `applicationEngineEnvironment`.

Inside the `applicationEngineEnvironment`, you can define HTTP and HTTPS connectors:

*To define a HTTP connector:*

```kotlin
connector {
    host = "0.0.0.0"
    port = 9090
}
```

*To define a HTTPS connector:*

```kotlin
sslConnector(keyStore = keyStore, keyAlias = "mykey", keyStorePassword = { "changeit".toCharArray() }, privateKeyPassword = { "changeit".toCharArray() }) {
    port = 9091
    keyStorePath = keyStoreFile.absoluteFile
}
```

*An actual example:*

```kotlin
fun main(args: Array<String>) {
    val env = applicationEngineEnvironment {
        module {
            main()
        }
        // Private API
        connector {
            host = "127.0.0.1"
            port = 9090
        }
        // Public API
        connector {
            host = "0.0.0.0"
            port = 8080
        }
    }
    embeddedServer(Netty, env).start(true)
}
```

The application will handle all the connections. You have access to the local port for each ApplicationCall,
so you can decide what to do based on the local port:

```kotlin
fun Application.main() {
    routing {
        get("/") {
            if (call.request.local.port == 8080) {
                call.respondText("Connected to public api")
            } else {
                call.respondText("Connected to private api")
            }
        }
    }
}
```

You can see a complete example of this in [ktor-samples/multiple-connectors](https://github.com/ktorio/ktor-samples/tree/1.3.0/other/multiple-connectors).



## Available configuration parameters
{id="available-config"}

There is a list of properties which Ktor understands out of the box and that you can
pass from the command line or the HOCON file.

**Switch** refers to command line arguments that you pass to the application, so you can, for example, change the bound port by:

`java -jar myapp-fatjar.jar -port=8080`

**Parameter paths** are paths inside the `application.conf` file:

```text
ktor.deployment.port = 8080
```

```text
ktor {
    deployment {
        port = 8080
    }
}
```

General switches and parameters:

| Switch          | Parameter path                         | Default               | Description |
|-----------------|:---------------------------------------|:----------------------|:------------|
| `-jar=`         |                                        |                       | Path to JAR file |
| `-config=`      |                                        |                       | Path to config file (instead of `application.conf` from resources) |
| `-host=`        | `ktor.deployment.host`                 | `0.0.0.0`             | Bound host |
| `-port=`        | `ktor.deployment.port`                 | `80`                  | Bound port |
| `-watch=`       | `ktor.deployment.watch`                | `[]`                  | Package paths to watch for reloading |
|                 | `ktor.application.id`                  | `Application`         | Application Identifier used for logging |
|                 | `ktor.deployment.rootPath`             | `/`                   | Servlet context path |
|                 | `ktor.deployment.callGroupSize`        | `parallelism`         | Event group size running application code |
|                 | `ktor.deployment.connectionGroupSize`  | `parallelism / 2 + 1` | Event group size accepting connections |
|                 | `ktor.deployment.workerGroupSize`      | `parallelism / 2 + 1` | Event group size for processing connections, parsing messages and doing engine's internal work |
|                 | `ktor.deployment.shutdown.url`         |                       | URL for shutdown the application when defined. Internally uses the [ShutDownUrl feature](shutdown-url.md) |
{ .styled-table #general }

Required when SSL port is defined:

| Switch          | Parameter path                         | Default          | Description |
|-----------------|:---------------------------------------|:-----------------|:------------|
| `-sslPort=`     | `ktor.deployment.sslPort`              | `null`           | SSL port    |
| `-sslKeyStore=` | `ktor.security.ssl.keyStore`           | `null`           | SSL key store |
|                 | `ktor.security.ssl.keyAlias`           | `mykey`          | Alias for the SSL key store |
|                 | `ktor.security.ssl.keyStorePassword`   | `null`           | Password for the SSL key store |
|                 | `ktor.security.ssl.privateKeyPassword` | `null`           | Password for the SSL private key |
{ .styled-table #ssql}

You can use `-P:` to specify parameters that don't have a specific switch. For example:
`-P:ktor.deployment.callGroupSize=7`.

## Reading the configuration from code
{id="accessing-config"}

If you are using a `EngineMain` instead of an `embeddedServer`, the HOCON file is loaded,
and you are able to access its configuration properties.

You can also define arbitrary property paths to configure your application.

```kotlin
val port: String = application.environment.config
    .propertyOrNull("ktor.deployment.port")?.getString()
    ?: "80"
```

It is possible to access the HOCON `application.conf` configuration too, by using a custom main with commandLineEnvironment:

```kotlin
embeddedServer(Netty, commandLineEnvironment(args + arrayOf("-port=8080"))).start(true)
```

Or by redirecting it to the specific `EngineMain.main`:

```kotlin
val moduleName = Application::module.javaMethod!!.let { "${it.declaringClass.name}.${it.name}" }
io.ktor.server.netty.main(args + arrayOf("-port=8080", "-PL:ktor.application.modules=$moduleName"))
```

Or with a custom `applicationEngineEnvironment`:

```kotlin
embeddedServer(Netty, applicationEngineEnvironment {
    log = LoggerFactory.getLogger("ktor.application")
    config = HoconApplicationConfig(ConfigFactory.load()) // Provide a Hocon config file

    module {
        routing {
            get("/") {
                call.respondText("HELLO")
            }
        }
    }

    connector {
        port = 8080
        host = "127.0.0.1"
    }
}).start(true)
```

You can also access the configuration properties by manually loading the default config file `application.conf`:

```kotlin
val config = HoconApplicationConfig(ConfigFactory.load())
```

## Using environment variables
{id="environment-variables"}

For *HOCON*, if you want to configure some parameters using environment variables,
you can use environment substitution using `${ENV}` syntax. For example:

```groovy
ktor {
    deployment {
        port = ${PORT}
    }
}
```

This will look for a `PORT` environment variable, and if not found an exception will be thrown:

```text
Exception in thread "main" com.typesafe.config.ConfigException$UnresolvedSubstitution: application.conf @ file:/path/to/application.conf: 3: Could not resolve substitution to a value: ${PORT}
```

In case you want to provide a default value for a property because the environment doesn't exist,
you can set the property with the default value, and then set it again with the `${?ENV}` syntax:

```groovy
ktor {
    deployment {
        port = 8080
        port = ${?PORT}
    }
}
```

If you are using `embeddedServer` you can still use `System.getenv` from Java. For example:

```kotlin
val port = System.getenv("PORT")?.toInt() ?: 8080
```

## Custom configuration systems
{id="custom"}

Ktor provides an interface that you can implement the configuration in, available at `application.environment.config`.
You can construct and set the configuration properties inside an `applicationEngineEnvironment`.

```kotlin
interface ApplicationConfig {
    fun property(path: String): ApplicationConfigValue
    fun propertyOrNull(path: String): ApplicationConfigValue?
    fun config(path: String): ApplicationConfig
    fun configList(path: String): List<ApplicationConfig>
}

interface ApplicationConfigValue {
    fun getString(): String
    fun getList(): List<String>
}

class ApplicationConfigurationException(message: String) : Exception(message)
```

Ktor provides two implementations. One based on a map (`MapApplicationConfig`), and other based on HOCON (`HoconApplicationConfig`).

You can create and compose config implementations and set them at `applicationEngineEnvironment`, so it is available to all the
application components.


