[//]: # (title: Application Configuration)

<show-structure for="chapter" depth="4"/>

<link-summary>
Learn how to configure various engine, and custom application parameters in code and file-based configurations.
</link-summary>

When running a Ktor server application, you might need to configure Ktor and application-specific configuration.
These kinds of configurations are unrelated to [Engine Configuration][server-engines.md] and allow configuring Ktor
itself or your own configurations required for your application.

### Overview {id="configuration-overview"}

Ktor supports loading [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) and [YAML](https://yaml.org)
configuration files out-of-the-box.
If you use [EngineMain](server-create-and-configure.topic#engine-main) to start a server, Ktor will automatically load
`application.conf` or `application.yaml` respectively.
[EmbeddedServer](server-create-and-configure.topic#embedded-server) will not automatically load any configuration files,
but you can rely on `ApplicationConfig` directly to load them manually.

<note>
To use a YAML configuration file, you need to add the `ktor-server-config-yaml` [dependency](#server-dependencies.topic).
</note>

Both `YAML` and `HOCON` support environment variable substitution using `"$NAME:fallback"` syntax. This is convenient as
it allows us to easily parameterize configuration values and provide fallback values.
For more complex use-cases you can also load [environment-specific configuration files](#environment-config) or override
configuration values by passing [command-line arguments](#command-line).

When working with `ApplicationConfig` directly, we can use `getAs` to retrieve a deserialized `DatabaseConfig` from our
loaded configurations. `EngineMain` can rely on the `application.yaml` that was automatically loaded.

<tabs>
<tab title="EmbeddedServer.kt">

```kotlin
@Serializable
data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String
)

embeddedServer(Netty) {
    val config = ApplicationConfig("application.yaml")
    val databaseConfig = config.property("database")
        .getAs<DatabaseConfig>()
}
```

</tab>
<tab title="EngineMain.kt">

```kotlin
@Serializable
data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String
)

fun Application.module() {
    val databaseConfig = property<DatabaseConfig>("database")
}
```

</tab>
<tab title="application.yaml">

```yaml
database:
  jdbcUrl: "$DB_JDBC_URL:jdbc:postgresql://localhost/ktor_database"
  username: "$DB_USERNAME:ktor_user"
  password: "$DB_PASSWORD:ktor_password"
```

</tab>
<tab title="application.conf">

```yaml
database {
  jdbcUrl="$DB_JDBC_URL:jdbc:postgresql://localhost/ktor_database"
  username="$DB_USERNAME:ktor_user"
  password="$DB_PASSWORD:ktor_password"
}
```

</tab>
</tabs>

### Layered Configuration Files {id="layered-config"}

In some cases it's desired to _split_ configuration files based on the environment to prevent a fallback value from
being used in production.
Or to define a _main_ configuration and only override specific keys depending on the environment. The configuration
below defines an `application.yaml` which holds our _base_ configuration.
The environment-specific files can override key-value pairs defined by the _base_ configuration.
In this case `application-prod.yaml` overrides the `loglevel`, whilst loading `application-test.yaml` will override
`database` and configure an additional `dev-only-key` configuration value.

<tabs>
<tab title="application.yaml">

```yaml
ktor:
  application.modules:
    - org.jetbrains.Application.module

database:
  jdbcUrl: "$DB_JDBC_URL"
  username: "$DB_USERNAME"
  password: "$DB_PASSWORD"

loglevel: INFO
```

</tab>
<tab title="application-prod.yaml">

```yaml
loglevel: ERROR
```

</tab>
<tab title="application-test.yaml">

```yaml
database:
  jdbcUrl: "jdbc:postgresql://localhost/ktor_database"
  username: "ktor_user"
  password: "ktor_password"

dev-only-key: "Extra configuration only available in test"
```

</tab>
</tabs>

`embeddedServer` doesn't load any files by itself, we first load `application.yaml`, and we `mergeWith` our
environment-specific configuration file.
`mergeWith` will override any existing keys from the original configuration and add all non-existing ones.
When overriding which files `EngineMain` should load, we need to specify _all_ files. Most conveniently, this is done by
passing CLI arguments specific for your environment, but this can also be done through code by explicitly setting the
arguments.

<tabs>
<tab title="EmbeddedServer.kt">

```kotlin
@Serializable
enum class LogLevel { ERROR, INFO; }

@Serializable
data class AppConfig(val level: LogLevel, val database: DatabaseConfig)

embeddedServer(Netty) {
    val env = System.getenv("ENV") ?: "test"
    val config = ApplicationConfig("application.yaml")
        .mergeWith(ApplicationConfig("application-$env.yaml"))
    val appConfig = config.property("app").getAs<AppConfig>()
}
```

</tab>
<tab title="EngineMain.kt">

```kotlin
@Serializable
data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String
)

fun main(args: Array<String>) {
    val environment = System.getenv("KTOR_ENVIRONMENT") ?: "test"
    val configs = arrayOf(
        "-config=application.yaml",
        "-config=application-$environment.yaml"
    )
    EngineMain.main(args + configs)
}

fun Application.module() {
    val appConfig = property<AppConfig>("app")
}
```

</tab>
<tab title="EngineMain CLI">

```shell
java -jar sample-app.jar -config=application.conf,application-prod.conf
```

</tab>
</tabs>

### Command-line arguments {id="command-line"}

When working with `EngineMain` ktor allows you to override configuration properties using command-line arguments.
This is useful when you need to change the configuration without modifying the configuration file.

To override a property, use the `-P:` prefix followed by the property path and value:

```shell
java -jar sample-app.jar -P:ktor.deployment.port=8080
```

In this example, we override the `ktor.deployment.port` property with the value `8080`.

You can also use environment variables to override properties:

```shell
PORT=8080 java -jar sample-app.jar
```

In this example, we set the `PORT` environment variable, which will be used if the configuration file contains
`${?PORT}` as shown in the [ktor.deployment](#deployment) section.

#### Multiple configuration files {id="multiple-configuration-files"}

Ktor allows you to use multiple configuration files. This is useful when you need to have different configurations for
different environments (development, testing, production).

To specify a configuration file, use the `-config=` command-line argument:

```shell
java -jar sample-app.jar -config=application-prod.conf
```

In this example, we use the `application-prod.conf` file instead of the default `application.conf`.

You can also combine multiple configuration files:

```shell
java -jar sample-app.jar -config=application.conf,application-prod.conf
```

In this case, properties from `application-prod.conf` will override properties from `application.conf` if they have the
same path.


