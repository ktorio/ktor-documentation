[//]: # (title: Webjars)

<var name="plugin_name" value="Webjars"/>
<var name="artifact_name" value="ktor-server-webjars"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="webjars"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>


The `%plugin_name%` plugin enables serving the client-side libraries provided by [WebJars](https://www.webjars.org/). It allows you to package your assets such as JavaScript and CSS libraries as part of your [fat JAR](fatjar.md).

## Add dependencies {id="add_dependencies"}
To enable `%plugin_name%`, you need to include the following artifacts in the build script:
* Add the `%artifact_name%` dependency:
  <include src="lib.xml" include-id="add_ktor_artifact"/>

* Add a dependency for a required client-side library. The example below shows how to add a Bootstrap artifact:
  <var name="group_id" value="org.webjars"/>
  <var name="artifact_name" value="bootstrap"/>
  <var name="version" value="bootstrap_version"/>
  <include src="lib.xml" include-id="add_artifact"/>
  You can replace `bootstrap_version` with the required version of the `bootstrap` artifact, for example, `%bootstrap_version%`.

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

By default, `%plugin_name%` serves WebJars assets on the `/webjars` path. The example below shows how to change this and serve any WebJars assets on the `/assets/` path:

```kotlin
```
{src="snippets/webjars/src/main/kotlin/com/example/Application.kt" lines="11-13"}

For instance, if you've installed the `org.webjars:bootstrap` dependency, you can add `bootstrap.css` as follows:

```html
```
{src="snippets/webjars/src/main/resources/files/index.html" lines="3,8-9"}

Note that `%plugin_name%` allows you to change the versions of the dependencies without changing the path used to load them.

> You can find the full example here: [webjars](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/webjars).
