[//]: # (title: JTE)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>

<var name="plugin_name" value="Jte"/>
<var name="package_name" value="io.ktor.server.jte"/>
<var name="artifact_name" value="ktor-server-jte"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="jte"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

Ktor allows you to use [JTE templates](https://github.com/casid/jte) as views within your application by installing the [%plugin_name%](https://api.ktor.io/ktor-server-jte/io.ktor.server.jte/-jte.html) plugin.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

To handle `.kte` files, add the `gg.jte:jte-kotlin` artifact to your project:

<var name="group_id" value="gg.jte"/>
<var name="artifact_name" value="jte-kotlin"/>
<var name="version" value="jte_version" />
<include from="lib.topic" element-id="add_artifact"/>

> The current `jte‑kotlin` compiler plugin is not compatible with Kotlin 2.3.x.
> Since Ktor 3.4.0 uses the Kotlin 2.3 toolchain, the Ktor JTE plugin cannot be used until the `jte‑kotlin`
> plugin adds support for Kotlin 2.3.
> 
> If you rely on JTE, use Kotlin 2.2.x and a compatible Ktor version until `jte‑kotlin` is updated for Kotlin 2.3.
> 
{style="warning"}

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
{src="snippets/jte/src/main/kotlin/com/example/Application.kt" include-lines="3-6,9-10,13-17,24"}

### Send a template in response {id="use_template"}
Suppose you have the `index.kte` template in the `templates` directory:
```html
```
{src="snippets/jte/templates/index.kte"}

To use the template for the specified [route](server-routing.md), pass `JteContent` to the `call.respond` method in the following way:
```kotlin
```
{src="snippets/jte/src/main/kotlin/com/example/Application.kt" include-lines="19-22"}
