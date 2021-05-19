[//]: # (title: HTML DSL)

<microformat>
<var name="example_name" value="html-widget"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

HTML DSL integrates the [kotlinx.html](https://github.com/Kotlin/kotlinx.html) library into Ktor and allows you to respond to a client with HTML blocks. With HTML DSL, you can write pure HTML in Kotlin, interpolate variables into views, and even build complex HTML layouts using templates.


## Add dependencies {id="add_dependencies"}
HTML DSL doesn't need [installation](Features.md#install) but requires the `ktor-html-builder` artifact. You can include it in the build script as follows:

<var name="artifact_name" value="ktor-html-builder"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

Note that this artifact depends on `kotlinx-html-jvm`, which is placed on a [Space Packages](https://www.jetbrains.com/help/space/packages.html) repository:

<tabs>
<tab title="Gradle (Groovy)">
<code style="block" lang="Groovy" title="Sample">
repositories {
    maven { url "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven" }
}
</code>
</tab>
<tab title="Gradle (Kotlin)">
<code style="block" lang="Kotlin" title="Sample">
repositories {
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}
</code>
</tab>
<tab title="Maven">
<code style="block" lang="XML" title="Sample" interpolate-variables="true">
<![CDATA[
    <repositories>
        <repository>
            <id>kotlinx-html</id>
            <url>https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven</url>
        </repository>
    </repositories>
]]>
</code>
</tab>
</tabs>

  

## Send HTML in response {id="html_response"}
To send an HTML response, call the [ApplicationCall.respondHtml](https://api.ktor.io/%ktor_version%/io.ktor.html/respond-html.html) method inside the required [route](Routing_in_Ktor.md):
```kotlin
```
{src="snippets/html/src/main/kotlin/com/example/Application.kt" lines="12-26"}

In this case, the following HTML will be sent to the client:
```html
<head>
    <title>Ktor</title>
</head>
<body>
    <h1>Hello from Ktor!</h1>
</body>

```
To learn more about generating HTML using kotlinx.html, see the [kotlinx.html wiki](https://github.com/Kotlin/kotlinx.html/wiki).


## Templates {id="templates"}

In addition to generating plain HTML, Ktor provides a template engine that can be used to build complex layouts. You can create a hierarchy of templates for different parts of an HTML page, for example, a root template for the entire page, child templates for a page header and footer, and so on. Ktor exposes the following API for working with templates:

1. To respond with an HTML built based on a specified template, call the [ApplicationCall.respondHtmlTemplate](https://api.ktor.io/%ktor_version%/io.ktor.html/respond-html-template.html) method.
1. To create a template, you need to implement the [Template](https://api.ktor.io/%ktor_version%/io.ktor.html/-template/index.html) interface and override the [Template.apply](https://api.ktor.io/%ktor_version%/io.ktor.html/-template/apply.html) method providing HTML.
1. Inside a created template class, you can define placeholders for different content types:
    * [Placeholder](https://api.ktor.io/%ktor_version%/io.ktor.html/-placeholder/index.html) is used to insert the content. [PlaceholderList](https://api.ktor.io/%ktor_version%io.ktor.html/-placeholder-list/index.html) can be used to insert the content that appears multiple times (for example, list items).
    * [TemplatePlaceholder](https://api.ktor.io/%ktor_version%/io.ktor.html/-template-placeholder/index.html) can be used to insert child templates and create nested layouts.
    

### Example {id="example"}
Let's see the example of how to create a hierarchical layout using templates. Imagine we have the following HTML:
```html
<body>
<h1>Ktor</h1>
<article>
    <h2>Hello from Ktor!</h2>
    <p>Kotlin Framework for creating connected systems.</p>
</article>
</body>
```
We can split the layout of this page into two parts:
* A root layout template for a page header and a child template for an article.
* A child template for the article content.

Let's implement these layouts step-by-step:
  
1. Call the `respondHtmlTemplate` method and pass a template class as a parameter. In our case, this is the `LayoutTemplate` class that should implement the `Template` interface:
```kotlin
get("/") {
    call.respondHtmlTemplate(LayoutTemplate()) {
        // ...
    }
}
```
Inside the block, we will be able to access a template and specify its property values. These values will substitute placeholders specified in a template class. We'll create `LayoutTemplate` and define its properties in the next step.
  
2. A root layout template will look in the following way:
```kotlin
class LayoutTemplate: Template<HTML> {
    val header = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<ContentTemplate>()
    override fun HTML.apply() {
        body {
            h1 {
                insert(header)
            }
            insert(ContentTemplate(), content)
        }
    }
}
```
The class exposes two properties:
* The `header` property specifies a content inserted within the `h1` tag.
* The `content` property specifies a child template for article content.

3. A child template will look as follows:
```kotlin
class ContentTemplate: Template<FlowContent> {
    val articleTitle = Placeholder<FlowContent>()
    val articleText = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        article {
            h2 {
                insert(articleTitle)
            }
            p {
                insert(articleText)
            }
        }
    }
}
```
This template exposes the `articleTitle` and `articleText` properties, whose values will be inserted inside the `article`.

4. Now we are ready to send HTML built using the specified property values:
```kotlin
get("/") {
    call.respondHtmlTemplate(LayoutTemplate()) {
        header {
            +"Ktor"
        }
        content {
            articleTitle {
                +"Hello from Ktor!"
            }
            articleText {
                +"Kotlin Framework for creating connected systems."
            }
        }
    }
}
```
