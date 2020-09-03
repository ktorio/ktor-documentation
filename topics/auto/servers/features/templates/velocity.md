[//]: # (title: Velocity)
[//]: # (caption: Using Velocity Templates)
[//]: # (category: servers)
[//]: # (keywords: html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.velocity.Velocity)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/velocity.html: - /features/velocity.html)
[//]: # (- /features/templates/velocity.html: - /features/templates/velocity.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor includes support for [Velocity](http://velocity.apache.org/) templates through the Velocity
feature.  Initialize the Velocity feature with the
[VelocityEngine](https://velocity.apache.org/engine/1.7/apidocs/org/apache/velocity/app/VelocityEngine.html):



## Installation
{id="installation"}

You can install Velocity, and configure the `VelocityEngine`.

```kotlin
install(Velocity) {
    setProperty("resource.loader", "classpath")
    setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
    }
}
```

## Usage
{id="usage"}

When Velocity is configured, you can call the `call.respond` method with a `VelocityContent` instance: 

```kotlin
data class User(val name: String, val email: String)

get("/") {
	 val user = User("user name", "user@example.com")
    call.respond(VelocityContent("templates/hello.vl", user))
}
```