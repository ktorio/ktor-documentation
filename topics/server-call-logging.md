[//]: # (title: Call logging)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>

<var name="plugin_name" value="CallLogging"/>
<var name="package_name" value="io.ktor.server.plugins.calllogging"/>
<var name="artifact_name" value="ktor-server-call-logging"/>
<var name="ktor_version_catalog_name" value="ktorLibs.server.callLogging"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="logging"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

Ktor provides the capability to log application events using the [SLF4J](http://www.slf4j.org/) library. You can learn
about general logging configuration from the [](server-logging.md) topic.

The `%plugin_name%` plugin allows you to log incoming client requests.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Configure logging settings {id="configure"}

You can configure `%plugin_name%` in multiple ways: specify a logging level, filter requests based on a specified
condition, customize log messages, and so on. You can see the available configuration settings
at [CallLoggingConfig](https://api.ktor.io/ktor-server-call-logging/io.ktor.server.plugins.calllogging/-call-logging-config/index.html).

### Set the logging level {id="logging_level"}

By default, Ktor uses the `Level.INFO` logging level. To change it, use the `level` property:

```kotlin
```

{src="snippets/logging/src/main/kotlin/com/example/Application.kt" include-lines="14-15,25"}

### Filter log requests {id="filter"}

The `filter` property allows you to add conditions for filtering requests. In the example below, only requests made
to `/api/v1` get into a log:

```kotlin
```

{src="snippets/logging/src/main/kotlin/com/example/Application.kt" include-lines="14,16-18,25"}

### Customize a log message format {id="format"}

By using the `format` function, you can put any data related to a request/response into a log. The example below shows
how to log a response status, a request HTTP method, and the `User-Agent` header value for each request.

```kotlin
```

{src="snippets/logging/src/main/kotlin/com/example/Application.kt" include-lines="14,19-25"}

You can find the full example
here: [logging](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/logging).

### Put call parameters in MDC {id="mdc"}

The `%plugin_name%` plugin supports MDC (Mapped Diagnostic Context). You can put a desired context value with the
specified name to MDC using the `mdc` function. For example, in the code snippet below, a `name` query parameter is
added to MDC:

```kotlin
install(CallLogging) {
    mdc("name-parameter") { call ->
        call.request.queryParameters["name"]
    }
}
```

You can access the added value during an `ApplicationCall` lifetime:

```kotlin
import org.slf4j.MDC
// ...
MDC.get("name-parameter")
```

### Put authenticated user information in MDC {id="mdc-authenticated-user"}

Since the `mdc` provider receives the whole `ApplicationCall`, it can also put information about an authenticated
user into MDC. The example below adds the name of the authenticated principal to MDC:

```kotlin
install(CallLogging) {
    mdc("user-name") { call ->
        call.principal<UserIdPrincipal>()?.name
    }
}
```

The `call.principal()` function requires the `io.ktor.server.auth.principal` import and retrieves the principal
provided by the configured [authentication](server-auth.md) provider, such as
`UserIdPrincipal` used by [basic authentication](server-basic-auth.md).

MDC providers are evaluated each time `%plugin_name%` establishes the MDC context for a call: before the
[monitoring and plugin phases](server-custom-plugins-base-api.md#pipelines) of the request pipeline, before route
handlers, and when a completion log message is written after the response is sent. This has the following
consequences:

- If a provider returns `null`, for example because the principal is not authenticated yet, the value is omitted
  from MDC and the provider is re-evaluated next time.
- Once a provider returns a value, it is cached and reused for the rest of the call.
- All MDC values are removed after the call completes.

So it is safe to call `call.principal()` inside an MDC provider: authentication runs in the validation phase of the
request pipeline, and the provider is re-evaluated after that, so the authenticated user appears in MDC for your
route handlers and in the completion log message.

### Establish MDC in custom code {id="mdc-custom-code"}

The MDC context is available only to code executed inside the coroutine context established by `%plugin_name%`.
If your code runs outside this context, for example in a custom [plugin](server-custom-plugins-base-api.md)
installed in an earlier phase, you can establish the configured MDC context explicitly using
`application.mdcProvider.withMDCBlock()`:

```kotlin
import io.ktor.server.logging.mdcProvider
// ...
call.application.mdcProvider.withMDCBlock(call) {
    // MDC values configured with the mdc() function are available here
}
```

`Application.mdcProvider` resolves the MDC provider installed by `%plugin_name%`. If no such plugin is installed,
`withMDCBlock` simply executes the block without setting up MDC.
