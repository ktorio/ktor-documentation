[//]: # (title: HTTP request lifecycle)

<primary-label ref="server-plugin"/>

<var name="plugin_name" value="HttpRequestLifecycle"/>
<var name="example_name" value="server-http-request-lifecycle"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-core</code>
</p>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Supported engines</b>: <code>Netty</code>, <code>CIO</code>
</p>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

By default, Ktor continues processing a request even if the client disconnects. The
[`%plugin_name%`](https://api.ktor.io/ktor-http/io.ktor.http/-http-request-lifecycle.html)
plugin allows you to cancel request processing as soon as the client disconnects.

This is useful for long-running or resource-intensive requests that should stop executing when the client is no longer
waiting for a response.


## Install and configure %plugin_name% {id="install_plugin"}

To enable the `HttpRequestLifecycle` plugin, install it in your application module using the `install` function and
set the `cancelCallOnClose` property:

```kotlin
```
{src="snippets/server-http-request-lifecycle/src/main/kotlin/com/example/Application.kt" include-lines="9-10,18-22,37"}


When `cancelCallOnClose` is enabled, the `%plugin_name%` plugin installs a cancellation handler per request. When a
client disconnects, only the coroutine handling that specific route is canceled.

Cancellation propagates through structured concurrency, so any child coroutines started from the request coroutine
(for example, using `launch` or `async`) are also canceled. A `CancellationException` is thrown at the next
suspension point.

## Handling cancellation {id="handle_cancellation"}

You can catch `CancellationException` to perform cleanup operations, such as releasing resources or stopping background
work:

```kotlin
```
{src="snippets/server-http-request-lifecycle/src/main/kotlin/com/example/Application.kt" include-lines="19-37"}

> Coroutine cancellation is cooperative. Blocking or CPU-bound code is not interrupted automatically.
> If request handling performs long-running work, call
> `call.coroutineContext.ensureActive()` to react to cancellation.
>
> To learn more, see
> [Coroutine cancellation](https://kotlinlang.org/docs/cancellation-and-timeouts.html).
{style="note"}

> For the full example, see [%example_name%](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/%example_name%).

## Limitations

This plugin is only fully supported on `CIO` and `Netty` engines. Engines based on servlets (or other unsupported engines)
cannot detect client disconnects reliably. Cancellation can only be detected when the server attempts to write a
response.