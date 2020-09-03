[//]: # (title: Environments)
[//]: # (caption: How to differentiate between environments)
[//]: # (category: servers)
[//]: # (ktor_version_review: 1.0.0)

You might want to do different things depending on whether your server is running locally or on your
production machine.

Ktor doesn't impose any way for doing this, but here are some guidelines you can use, in
case you were wondering about it.

## HOCON & ENV
{id="proposal "}

You can use the `application.conf` file to set a variable that will hold the environment, then check that variable
at runtime and decide what to do.
You can configure it to check an environment variable `KTOR_ENV` and to provide a default value `dev`.
Then in production you set the `KTOR_ENV=prod`

For example:

### application.conf:

```
ktor {
    environment = dev
    environment = ${?KTOR_ENV}
}
```

You can access this config from the application, and use some extension properties to make your life easier:

```kotlin
fun Application.module() {
    when {
        isDev -> {
            // Do things only in dev   
        }
        isProd -> {
            // Do things only in prod
        }
    }
    // Do things for all the environments
}

val Application.envKind get() = environment.config.property("ktor.environment").getString()
val Application.isDev get() = envKind == "dev"
val Application.isProd get() = envKind != "dev"
```