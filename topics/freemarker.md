[//]: # (title: FreeMarker)

<show-structure for="chapter" depth="2"/>

[freemarker_template_loading]: https://freemarker.apache.org/docs/pgui_config_templateloading.html

<var name="plugin_name" value="FreeMarker"/>
<var name="package_name" value="io.ktor.server.freemarker"/>
<var name="artifact_name" value="ktor-server-freemarker"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="freemarker"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Ktor allows you to use [FreeMarker templates](https://freemarker.apache.org/) as views within your application by installing the [FreeMarker](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-freemarker/io.ktor.server.freemarker/-free-marker) plugin.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install FreeMarker {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

Inside the `install` block, you can [configure](#configure) the desired [TemplateLoader][freemarker_template_loading] for loading FreeMarker templates.


## Configure FreeMarker {id="configure"}
### Configure template loading {id="template_loading"}
To load templates, you need to assign the desired [TemplateLoader][freemarker_template_loading] type to the `templateLoader` property. For example, the code snippet below enables Ktor to look up templates in the `templates` package relative to the current classpath:
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" include-lines="3-5,10-14,21"}

### Send a template in response {id="use_template"}
Imagine you have the `index.ftl` template in `resources/templates`:
```html
```
{src="snippets/freemarker/src/main/resources/templates/index.ftl"}

A data model for a user looks as follows:
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" include-lines="23"}

To use the template for the specified [route](Routing_in_Ktor.md), pass `FreeMarkerContent` to the `call.respond` method in the following way:
```kotlin
```
{src="snippets/freemarker/src/main/kotlin/com/example/Application.kt" include-lines="16-19"}
