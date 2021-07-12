[//]: # (title: CSS DSL)

<microformat>
<var name="example_name" value="caching-headers"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

CSS DSL extends [HTML DSL](html_dsl.md) and allows you to author stylesheets in Kotlin by using the [kotlin-css](https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-css/README.md) wrapper.

> Learn how to serve stylesheets as static content from [](Serving_Static_Content.md).


## Add dependencies {id="add_dependencies"}
CSS DSL doesn't need [installation](Plugins.md#install), but requires including the following artifacts in the build script:

1. The `ktor-html-builder` artifact for HTML DSL:
   <var name="artifact_name" value="ktor-html-builder"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>
   
1. The `kotlin-css-jvm` artifact for building CSS:
   <var name="group_id" value="org.jetbrains.kotlin-wrappers"/>
   <var name="artifact_name" value="kotlin-css"/>
   <var name="version" value="kotlin_css_version"/>
   <include src="lib.xml" include-id="add_artifact"/>
   You can replace `$kotlin_css_version` with the required version of the `kotlin-css` artifact, for example, `%kotlin_css_version%`.


## Use CSS DSL {id="use_css"}

To send a CSS response, you need to extend `ApplicationCall` by adding the `respondCss` method to serialize a stylesheet into a string and send it to the client with the `CSS` content type:

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/com/example/Application.kt" lines="49-51"}

Then, you can provide CSS inside the required [route](Routing_in_Ktor.md):

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/com/example/Application.kt" lines="35-45"}

Finally, you can use the specified CSS for an HTML document created with [HTML DSL](html_dsl.md):

```kotlin
```
{src="snippets/caching-headers/src/main/kotlin/com/example/Application.kt" lines="23-34"}

You can find the full example here: [caching-headers](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/caching-headers).
