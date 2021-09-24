[//]: # (title: Velocity)

[velocity_engine]: https://velocity.apache.org/engine/devel/apidocs/org/apache/velocity/app/VelocityEngine.html

<var name="plugin_name" value="Velocity"/>
<var name="artifact_name" value="ktor-server-velocity"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="velocity"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor allows you to use [Velocity templates](https://velocity.apache.org/engine/) as views within your application by installing the [Velocity](https://api.ktor.io/ktor-features/ktor-velocity/ktor-velocity/io.ktor.velocity/-velocity/index.html) plugin.


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install Velocity {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure Velocity {id="configure"}
### Configure template loading {id="template_loading"}
Inside the `install` block, you can configure the [VelocityEngine][velocity_engine]. For example, if you want to use templates from the classpath, use a resource loader for `classpath`:
```kotlin
```
{src="snippets/velocity/src/main/kotlin/com/example/Application.kt" lines="13-16"}

### Send a template in response {id="use_template"}
Imagine you have the `index.vl` template in `resources/templates`:
```html
```
{src="snippets/velocity/src/main/resources/templates/index.vl"}

A data model for a user looks as follows:
```kotlin
```
{src="snippets/velocity/src/main/kotlin/com/example/Application.kt" lines="25"}

To use the template for the specified [route](Routing_in_Ktor.md), pass `VelocityContent` to the `call.respond` method in the following way:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(VelocityContent("templates/index.vl", mapOf("user" to sampleUser)))
}
```
