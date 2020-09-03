[//]: # (title: HTML DSL)
[//]: # (caption: Emit HTML with a DSL)
[//]: # (category: servers)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.html.HtmlContent)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/html-dsl.html: - /features/html-dsl.html)
[//]: # (- /features/templates/html-dsl.html: - /features/templates/html-dsl.html)
[//]: # (ktor_version_review: 1.0.0)

This feature integrates with [kotlinx.html](https://github.com/Kotlin/kotlinx.html)
to directly emit HTML using Chunked transfer encoding without having to keep
memory for the whole HTML.

{% include feature.html %}

## Installing
{id="install "}

This feature doesn't require installation.

## Basic Usage
{id="basic-usage "}

When generating the response, instead of calling the `respond`/`respondText` methods, you have to call `ApplicationCall.respondHtml`:

```kotlin
call.respondHtml {
    head {
        title { +"Async World" }
    }
    body {
        h1(id = "title") {
            +"Title"
        }
    }
}
```

For documentation about generating HTML using kotlinx.html, please check [its wiki](https://github.com/kotlin/kotlinx.html/wiki/Getting-started).

## Templates & Layouts
{id="templates-and-layouts "}

In addition to plain HTML generation with the DSL, ktor exposes a simple typed templating engine.
You can use it to generate complex layouts in a typed way. It is pretty simple, yet powerful:

```kotlin
call.respondHtmlTemplate(MulticolumnTemplate()) {
    column1 {
        +"Hello, $name"
    }
    column2 {
        +"col2"
    }
}

class MulticolumnTemplate(val main: MainTemplate = MainTemplate()) : Template<HTML> {
    val column1 = Placeholder<FlowContent>()
    val column2 = Placeholder<FlowContent>()
    override fun HTML.apply() {
        insert(main) {
            menu {
                item { +"One" }
                item { +"Two" }
            }
            content {
                div("column") {
                    insert(column1)
                }
                div("column") {
                    insert(column2)
                }
            }
        }
    }
}

class MainTemplate : Template<HTML> {
    val content = Placeholder<HtmlBlockTag>()
    val menu = TemplatePlaceholder<MenuTemplate>()
    override fun HTML.apply() {
        head {
            title { +"Template" }
        }
        body {
            h1 {
                insert(content)
            }
            insert(MenuTemplate(), menu)
        }
    }
}

class MenuTemplate : Template<FlowContent> {
    val item = PlaceholderList<UL, FlowContent>()
    override fun FlowContent.apply() {
        if (!item.isEmpty()) {
            ul {
                each(item) {
                    li {
                        if (it.first) b {
                            insert(it)
                        } else {
                            insert(it)
                        }
                    }
                }
            }
        }
    }
}
```

You have to define classes implementing `Template<TFlowContent>`,
overriding the `TFlowContent.apply` method and optionally define
`Placeholder` or `TemplatePlaceholder` properties just like
in the example.

When generating the template with `call.respondHtmlTemplate(MulticolumnTemplate()) { }`,
you will get the template as receiver, and will be able to access the placeholders
defined as properties in a typed way.