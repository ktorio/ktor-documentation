[//]: # (title: Mustache)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>

[mustache_factory]: http://spullara.github.io/mustache/apidocs/com/github/mustachejava/MustacheFactory.html

<var name="plugin_name" value="Mustache"/>
<var name="package_name" value="io.ktor.server.mustache"/>
<var name="artifact_name" value="ktor-server-mustache"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="mustache"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

Ktor allows you to use [Mustache templates](https://github.com/spullara/mustache.java) as views within your application by installing the [Mustache](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-mustache/io.ktor.server.mustache/-mustache) plugin.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install Mustache {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

Inside the `install` block, you can [configure](#template_loading) the [MustacheFactory][mustache_factory] for loading Mustache templates.


## Configure Mustache {id="configure"}
### Configure template loading {id="template_loading"}
To load templates, you need to assign the [MustacheFactory][mustache_factory] to the `mustacheFactory` property. For example, the code snippet below enables Ktor to look up templates in the `templates` package relative to the current classpath:
```kotlin
```
{src="snippets/mustache/src/main/kotlin/com/example/Application.kt" include-lines="3-6,11-15,22"}

### Send a template in response {id="use_template"}
Imagine you have the `index.hbs` template in `resources/templates`:
```html
```
{src="snippets/mustache/src/main/resources/templates/index.hbs"}

A data model for a user looks as follows:
```kotlin
```
{src="snippets/mustache/src/main/kotlin/com/example/Application.kt" include-lines="24"}

To use the template for the specified [route](server-routing.md), pass `MustacheContent` to the `call.respond` method in the following way:
```kotlin
```
{src="snippets/mustache/src/main/kotlin/com/example/Application.kt" include-lines="17-20"}
