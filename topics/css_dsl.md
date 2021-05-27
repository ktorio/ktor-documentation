[//]: # (title: CSS DSL)

CSS DSL extends [HTML DSL](html_dsl.md) and allows you to author stylesheets in Kotlin by using the [kotlin-css](https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-css/README.md) wrapper.

> Learn how to serve stylesheets as static content from [](Serving_Static_Content.md).


## Add dependencies {id="add_dependencies"}
CSS DSL doesn't need [installation](Features.md#install), but requires including the following artifacts in the build script:

1. The `ktor-html-builder` artifact for HTML DSL:
   <var name="artifact_name" value="ktor-html-builder"/>
   <include src="lib.xml" include-id="add_ktor_artifact"/>
   
1. The `kotlin-css-jvm` artifact for building CSS:
   <var name="group_id" value="org.jetbrains"/>
   <var name="artifact_name" value="kotlin-css-jvm"/>
   <var name="version" value="kotlin_css_version"/>
   <include src="lib.xml" include-id="add_artifact"/>
   You can replace `$kotlin_css_version` with the required version of the `kotlin-css-jvm` artifact, for example, `%kotlin_css_version%`.


## Use CSS DSL {id="use_css"}

To send a CSS response, you need to extend `ApplicationCall` by adding the `respondCss` method to serialize a stylesheet into a string and send it to the client with the `CSS` content type:

```kotlin
suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
   this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
```

Then, you can provide CSS inside the required [route](Routing_in_Ktor.md):

```kotlin
get("/styles.css") {
    call.respondCss {
        body {
            backgroundColor = Color.darkBlue
            margin(0.px)
        }
        rule("h1.page-title") {
            color = Color.white
        }
    }
}
```

Finally, you can use the specified CSS for an HTML document created with [HTML DSL](html_dsl.md):
```kotlin
get("/html-dsl") {
    call.respondHtml {
        head {
            link(rel = "stylesheet", href = "/styles.css", type = "text/css")
        }
        body {
            h1(classes = "page-title") {
                +"Hello from Ktor!"
            }
        }
    }
}
```
