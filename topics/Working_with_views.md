[//]: # (title: Templating)

<link-summary>Ktor provides different ways for working with views: you can build HTML/CSS using Kotlin DSL, or you can choose between JVM template engines, such as FreeMarker, Velocity, and so on.</link-summary>

Ktor provides different ways for working with views.

The following two libraries provide a Kotlin DSL for creating HTML and CSS. They are in part generated from the official HTML and CSS specifications. Combined with Kotlin's typing discipline they provide strong guarantees on the validity of their output and a great developer experience. These DSLs [do not render very fast](https://github.com/xmlet/template-benchmark), but given the time most web applications spend on rendering is small to begin with, this is likely not a dealbreaker except for the most performance critical situations. The compile speed of these DSLs is a bigger concern at this point: using these libraries for a large templating codebase will [slow down compilation](https://youtrack.jetbrains.com/issue/KT-51416/Compilation-of-kotlinx-html-DSL-should-still-be-faster) considerably.

* [](html_dsl.md)
* [](css_dsl.md)

Ktor also integrates with common JVM templating engines.

* [](freemarker.md)
* [](velocity.md)
* [](mustache.md)
* [](thymeleaf.md)
* [](pebble.md)
* [](jte.md) (allows using Kotlin in templates)

> To learn how to serve static HTML files, see [](Serving_Static_Content.md).
