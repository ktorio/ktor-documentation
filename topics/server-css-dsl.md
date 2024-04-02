[//]: # (title: CSS DSL)

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-html-builder</code>, <code>org.jetbrains.kotlin-wrappers:kotlin-css</code>
</p>
<var name="example_name" value="css-dsl"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

CSS DSL extends [HTML DSL](server-html-dsl.md) and allows you to author stylesheets in Kotlin by using the [kotlin-css](https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-css/README.md) wrapper.

> Learn how to serve stylesheets as static content from [](server-static-content.md).


## Add dependencies {id="add_dependencies"}
CSS DSL doesn't need [installation](server-plugins.md#install), but requires including the following artifacts in the build script:

1. The `ktor-server-html-builder` artifact for HTML DSL:

   <var name="artifact_name" value="ktor-server-html-builder"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
   
2. The `kotlin-css-jvm` artifact for building CSS:

   <var name="group_id" value="org.jetbrains.kotlin-wrappers"/>
   <var name="artifact_name" value="kotlin-css"/>
   <var name="version" value="kotlin_css_version"/>
   <include from="lib.topic" element-id="add_artifact"/>
   
   You can replace `$kotlin_css_version` with the required version of the `kotlin-css` artifact, for example, `%kotlin_css_version%`.


## Serve CSS {id="serve_css"}

To send a CSS response, you need to extend `ApplicationCall` by adding the `respondCss` method to serialize a stylesheet into a string and send it to the client with the `CSS` content type:

```kotlin
```
{src="snippets/css-dsl/src/main/kotlin/com/example/Application.kt" include-lines="39-41"}

Then, you can provide CSS inside the required [route](server-routing.md):

```kotlin
```
{src="snippets/css-dsl/src/main/kotlin/com/example/Application.kt" include-lines="25-35"}

Finally, you can use the specified CSS for an HTML document created with [HTML DSL](server-html-dsl.md):

```kotlin
```
{src="snippets/css-dsl/src/main/kotlin/com/example/Application.kt" include-lines="13-24"}

You can find the full example here: [css-dsl](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/css-dsl).
