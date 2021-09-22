[//]: # (title: Redirect)

<include src="lib.xml" include-id="outdated_warning"/>

By default, Ktor HTTP client does follow redirections. This plugin allows you to follow `Location` redirects in a way that works with any HTTP engine. Its usage is pretty straightforward, and the only configurable property is the `maxJumps` (20 by default) that limits how many redirects are tried before giving up (to prevent infinite redirects).



## Install

This plugin is installed by default.

## Prevent installing

```kotlin
val client = HttpClient(HttpClientEngine) {
    followRedirects = false
}
```

>This plugin is included in the core of the HttpClient, so it is available always along the client.
>
{type="note"}