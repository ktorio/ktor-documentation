[//]: # (title: Multiplatform)
[//]: # (caption: Multiplatform)
[//]: # (category: kotlinx)
[//]: # (permalink: /kotlinx/multiplatform.html)
[//]: # (toc: true)
[//]: # (ktor_version_review: 1.0.0)

Kotlin Multi-Platform Projects (also called Kotlin MPP).

Kotlin 1.2 introduced an experimental multiplatform support. While still experimental in 1.3, it has improved significantly.

The idea behind it is to be able to write common code with a subset of common APIs available on all the platforms,
and then per platform specific things.

Multiplatform projects add a couple of new keywords to the Kotlin Language: `expect` and `actual`.

* `expect` is available for common projects to be able to define bodyless APIs that must be available in common projects,
but will have specific implementations per platform.
* `actual` is available for non-common projects (JVM, JS and Native) and must match the expect structure in each platform
that will be supported.

**Reference: <https://kotlinlang.org/docs/reference/multiplatform.html>**