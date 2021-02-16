[//]: # (title: Velocity)

[velocity_engine]: https://velocity.apache.org/engine/devel/apidocs/org/apache/velocity/app/VelocityEngine.html

Ktor allows you to use [Velocity templates](https://velocity.apache.org/engine/) as views within your application by installing the [Velocity](https://api.ktor.io/%ktor_version%/io.ktor.velocity/-velocity/index.html) feature.


## Add Dependencies {id="add_dependencies"}
<var name="feature_name" value="Velocity"/>
<var name="artifact_name" value="ktor-velocity"/>
<include src="lib.md" include-id="add_ktor_artifact_intro"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Install Velocity {id="install_feature"}

<var name="feature_name" value="Velocity"/>
<include src="lib.md" include-id="install_feature"/>




## Configure Velocity {id="configure"}
### Configure Template Loading {id="template_loading"}
Inside the `install` block, you can configure the [VelocityEngine][velocity_engine]. For example, if you want to use templates from the classpath, use a resource loader for `classpath`:
```kotlin
import io.ktor.velocity.*
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.apache.velocity.runtime.RuntimeConstants

install(Velocity) {
    setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
    setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
}
```

### Send a Template in Response {id="use_template"}
Imagine you have the `index.vl` template in `resources/templates`:
```html
<html>
    <body>
        <h1>Hello, $user.name</h1>
    </body>
</html>
```

A data model for a user looks as follows:
```kotlin
data class User(val id: Int, val name: String)
```

To use the template for the specified [route](Routing_in_Ktor.md), pass `VelocityContent` to the `call.respond` method in the following way:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(VelocityContent("templates/index.vl", mapOf("user" to sampleUser)))
}
```
