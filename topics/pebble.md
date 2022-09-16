[//]: # (title: Pebble)

<show-structure for="chapter" depth="2"/>

[pebble_engine_builder]: https://pebbletemplates.io/com/mitchellbosecke/pebble/PebbleEngine/Builder/

<var name="plugin_name" value="Pebble"/>
<var name="package_name" value="io.ktor.server.pebble"/>
<var name="artifact_name" value="ktor-server-pebble"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="pebble"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Ktor allows you to use [Pebble templates](https://pebbletemplates.io/) as views within your application by installing the [Pebble](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-pebble/io.ktor.server.pebble/-pebble) plugin.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install Pebble {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

Inside the `install` block, you can [configure](#configure) the [PebbleEngine.Builder][pebble_engine_builder] for loading Pebble templates.


## Configure Pebble {id="configure"}
### Configure template loading {id="template_loading"}
To load templates, you need to configure how to load templates using [PebbleEngine.Builder][pebble_engine_builder]. For example, the code snippet below enables Ktor to look up templates in the `templates` package relative to the current classpath:

```kotlin
```
{src="snippets/pebble/src/main/kotlin/com/example/Application.kt" include-lines="3-5,10-16,23"}

### Send a template in response {id="use_template"}
Imagine you have the `index.html` template in `resources/templates`:

```html
```
{src="snippets/pebble/src/main/resources/templates/index.html"}

A data model for a user looks as follows:

```kotlin
```
{src="snippets/pebble/src/main/kotlin/com/example/Application.kt" include-lines="25"}

To use the template for the specified [route](Routing_in_Ktor.md), pass `PebbleContent` to the `call.respond` method in the following way:

```kotlin
```
{src="snippets/pebble/src/main/kotlin/com/example/Application.kt" include-lines="18-21"}
