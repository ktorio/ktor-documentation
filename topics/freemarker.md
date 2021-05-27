[//]: # (title: FreeMarker)
[freemarker_template_loading]: https://freemarker.apache.org/docs/pgui_config_templateloading.html

<microformat>
<var name="example_name" value="freemarker"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

Ktor allows you to use [FreeMarker templates](https://freemarker.apache.org/) as views within your application by installing the [Freemarker](https://api.ktor.io/%ktor_version%/io.ktor.freemarker/-free-marker/index.html) plugin.


## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="FreeMarker"/>
<var name="artifact_name" value="ktor-freemarker"/>
<include src="lib.md" include-id="add_ktor_artifact_intro"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Install FreeMarker {id="install_feature"}

<var name="feature_name" value="FreeMarker"/>
<include src="lib.md" include-id="install_feature"/>

Inside the `install` block, you can [configure](#configure) the desired [TemplateLoader][freemarker_template_loading] for loading FreeMarker templates.


## Configure FreeMarker {id="configure"}
### Configure template loading {id="template_loading"}
To load templates, you need to assign the desired [TemplateLoader][freemarker_template_loading] type to the `templateLoader` property. For example, the code snippet below enables Ktor to look up templates in the `templates` package relative to the current classpath:
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" lines="12-14"}

### Send a template in response {id="use_template"}
Imagine you have the `index.ftl` template in `resources/templates`:
```html
```
{src="snippets/freemarker/src/main/resources/templates/index.ftl"}

A data model for a user looks as follows:
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" lines="23"}

To use the template for the specified [route](Routing_in_Ktor.md), pass `FreeMarkerContent` to the `call.respond` method in the following way:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(FreeMarkerContent("index.ftl", mapOf("user" to sampleUser)))
}
```
