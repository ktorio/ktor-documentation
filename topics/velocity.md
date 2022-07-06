[//]: # (title: Velocity)

[velocity_engine]: https://velocity.apache.org/engine/devel/apidocs/org/apache/velocity/app/VelocityEngine.html

<var name="plugin_name" value="Velocity"/>
<var name="package_name" value="io.ktor.server.velocity"/>
<var name="artifact_name" value="ktor-server-velocity"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="velocity"/>
<include src="lib.xml" include-id="download_example"/>
</tldr>

Ktor allows you to use [Velocity templates](https://velocity.apache.org/engine/) as views within your application by installing the [Velocity](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-velocity/io.ktor.server.velocity/-velocity) plugin.


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install Velocity {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

Optionally, you can install the `VelocityTools` plugin to have the capability to add standard and custom [Velocity tools](#velocity_tools).

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
```
{src="snippets/velocity/src/main/kotlin/com/example/Application.kt" lines="18-21"}


### Add Velocity tools {id="velocity_tools"}

If you've [installed](#install_plugin) the `VelocityTools` plugin, you can access the `EasyFactoryConfiguration` instance inside the `install` block to add standard and custom Velocity tools, for example:

```kotlin
install(VelocityTools) {
    engine {
        // Engine configuration
        setProperty("resource.loader", "string")
        addProperty("resource.loader.string.name", "myRepo")
        addProperty("resource.loader.string.class", StringResourceLoader::class.java.name)
        addProperty("resource.loader.string.repository.name", "myRepo")
    }
    addDefaultTools() // Add a default tool
    tool("foo", MyCustomTool::class.java) // Add a custom tool
}
```
