[//]: # (title: What's new in Ktor 3.3.0)

<show-structure for="chapter,procedure" depth="2"/>

## Ktor Server

### Custom fallback for static resources

Ktor 3.3.0 introduces a new `fallback()` function for static content, allowing you to define custom
behavior when a requested resource is not found.

Unlike `default()`, which always serves the same fallback file, `fallback()` gives you access to the original requested
path and the current `ApplicationCall`. You can use this to redirect, return custom status codes, or serve different
files dynamically.

To define custom fallback behaviour, use the `fallback()` function within `staticFiles()`, `staticResources()`, `staticZip()`, or
`staticFileSystem()`:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="22,25-32,45"}
