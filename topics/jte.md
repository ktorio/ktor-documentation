[//]: # (title: JTE)

<var name="plugin_name" value="Jte"/>
<var name="package_name" value="io.ktor.server.jte"/>
<var name="artifact_name" value="ktor-server-jte"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="jte"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Ktor allows you to use [JTE templates](https://github.com/casid/jte) as views within your application by installing the [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-jte/io.ktor.server.jte/-jte.html) plugin.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

> To handle `.kte` files, you need to add the `gg.jte:jte-kotlin` artifact to your project.

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

Inside the `install` block, you can [configure](#configure) how to load JTE templates.


## Configure %plugin_name% {id="configure"}
### Configure template loading {id="template_loading"}
To load JTE templates, you need to:
1. Create a `CodeResolver` used to resolve template code. For example, you can configure `DirectoryCodeResolver` to load templates from a given directory or `ResourceCodeResolver` to load templates from application resources.
2. Use the `templateEngine` property to specify a template engine, which uses a created `CodeResolver` to transfer templates into native Java/Kotlin code.

For instance, the code snippet below enables Ktor to look up JTE templates in the `templates` directory:

```kotlin
```
{src="snippets/jte/src/main/kotlin/com/example/Application.kt" lines="14-17"}

### Send a template in response {id="use_template"}
Suppose you have the `index.kte` template in the `templates` directory:
```html
```
{src="snippets/jte/templates/index.kte"}

To use the template for the specified [route](Routing_in_Ktor.md), pass `JteContent` to the `call.respond` method in the following way:
```kotlin
```
{src="snippets/jte/src/main/kotlin/com/example/Application.kt" lines="19-22"}
