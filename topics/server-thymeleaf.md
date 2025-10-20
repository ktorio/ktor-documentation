[//]: # (title: Thymeleaf)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>

<var name="plugin_name" value="Thymeleaf"/>
<var name="package_name" value="io.ktor.server.thymeleaf"/>
<var name="artifact_name" value="ktor-server-thymeleaf"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="thymeleaf"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

Ktor allows you to use [Thymeleaf templates](https://www.thymeleaf.org/) as views within your application by installing the [Thymeleaf](https://api.ktor.io/ktor-server-thymeleaf/io.ktor.server.thymeleaf/-thymeleaf) plugin.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install Thymeleaf {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>



## Configure Thymeleaf {id="configure"}
### Configure template loading {id="template_loading"}
Inside the `install` block, you can configure the `ClassLoaderTemplateResolver`. For example, the code snippet below enables Ktor to look up `*.html` templates in the `templates` package relative to the current classpath:
```kotlin
```
{src="snippets/thymeleaf/src/main/kotlin/com/example/Application.kt" include-lines="3,6-8,11-18,25"}

### Send a template in response {id="use_template"}
Imagine you have the `index.html` template in `resources/templates`:
```html
```
{src="snippets/thymeleaf/src/main/resources/templates/index.html"}

A data model for a user looks as follows:
```kotlin
```
{src="snippets/thymeleaf/src/main/kotlin/com/example/Application.kt" include-lines="27"}

To use the template for the specified [route](server-routing.md), pass [ThymeleafContent](https://api.ktor.io/ktor-server-thymeleaf/io.ktor.server.thymeleaf/-thymeleaf-content/index.html) to the `call.respond` method in the following way:
```kotlin
```
{src="snippets/thymeleaf/src/main/kotlin/com/example/Application.kt" include-lines="20-23"}


## Example: Auto reload Thymeleaf templates {id="auto-reload"}

The example below shows how to reload Thymeleaf templates automatically when [development mode](server-development-mode.topic) is used.

```kotlin
```
{src="snippets/thymeleaf-auto-reload/src/main/kotlin/com/example/Application.kt"}

You can find the complete example here: [thymeleaf-auto-reload](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/thymeleaf-auto-reload).


