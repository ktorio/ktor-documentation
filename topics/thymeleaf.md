[//]: # (title: Thymeleaf)

<microformat>
<var name="example_name" value="thymeleaf"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor allows you to use [Thymeleaf templates](https://www.thymeleaf.org/) as views within your application by installing the [Thymeleaf](https://api.ktor.io/ktor-features/ktor-thymeleaf/ktor-thymeleaf/io.ktor.thymeleaf/-thymeleaf/index.html) plugin (previously known as feature).


## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="Thymeleaf"/>
<var name="artifact_name" value="ktor-thymeleaf"/>
<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install Thymeleaf {id="install_feature"}

<var name="feature_name" value="Thymeleaf"/>
<include src="lib.xml" include-id="install_feature"/>



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
