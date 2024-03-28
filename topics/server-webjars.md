[//]: # (title: Webjars)

<var name="plugin_name" value="Webjars"/>
<var name="package_name" value="io.ktor.server.webjars"/>
<var name="artifact_name" value="ktor-server-webjars"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="webjars"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

<link-summary>
The %plugin_name% plugin enables serving the client-side libraries provided by WebJars.
</link-summary>


The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-webjars/io.ktor.server.webjars/-webjars.html) plugin enables serving the client-side libraries provided by [WebJars](https://www.webjars.org/). It allows you to package your assets such as JavaScript and CSS libraries as part of your [fat JAR](server-fatjar.md).

## Add dependencies {id="add_dependencies"}
To enable `%plugin_name%`, you need to include the following artifacts in the build script:
* Add the `%artifact_name%` dependency:

  <include from="lib.topic" element-id="add_ktor_artifact"/>

* Add a dependency for a required client-side library. The example below shows how to add a Bootstrap artifact:

  <var name="group_id" value="org.webjars"/>
  <var name="artifact_name" value="bootstrap"/>
  <var name="version" value="bootstrap_version"/>
  <include from="lib.topic" element-id="add_artifact"/>
  
  You can replace `$bootstrap_version` with the required version of the `bootstrap` artifact, for example, `%bootstrap_version%`.

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

By default, `%plugin_name%` serves WebJars assets on the `/webjars` path. The example below shows how to change this and serve any WebJars assets on the `/assets` path:

```kotlin
```
{src="snippets/webjars/src/main/kotlin/com/example/Application.kt" include-lines="3,6-7,10-13,19"}

For instance, if you've installed the `org.webjars:bootstrap` dependency, you can add `bootstrap.css` as follows:

```html
```
{src="snippets/webjars/src/main/resources/files/index.html" include-lines="3,8-9"}

Note that `%plugin_name%` allows you to change the versions of the dependencies without changing the path used to load them.

> You can find the full example here: [webjars](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/webjars).
