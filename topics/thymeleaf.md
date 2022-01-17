[//]: # (title: Thymeleaf)

<var name="plugin_name" value="Thymeleaf"/>
<var name="artifact_name" value="ktor-server-thymeleaf"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="thymeleaf"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor allows you to use [Thymeleaf templates](https://www.thymeleaf.org/) as views within your application by installing the [Thymeleaf](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-thymeleaf/io.ktor.server.thymeleaf/-thymeleaf/index.html) plugin.


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install Thymeleaf {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>



## Configure Thymeleaf {id="configure"}
### Configure template loading {id="template_loading"}
Inside the `install` block, you can configure the `ClassLoaderTemplateResolver`. For example, the code snippet below enables Ktor to look up `*.html` templates in the `templates` package relative to the current classpath:
```kotlin
```
{src="snippets/thymeleaf/src/main/kotlin/com/example/Application.kt" lines="12-18"}

### Send a template in response {id="use_template"}
Imagine you have the `index.html` template in `resources/templates`:
```html
```
{src="snippets/thymeleaf/src/main/resources/templates/index.html"}

A data model for a user looks as follows:
```kotlin
```
{src="snippets/thymeleaf/src/main/kotlin/com/example/Application.kt" lines="27"}

To use the template for the specified [route](Routing_in_Ktor.md), pass `ThymeleafContent` to the `call.respond` method in the following way:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(ThymeleafContent("index", mapOf("user" to sampleUser)))
}
```
