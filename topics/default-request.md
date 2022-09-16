[//]: # (title: Default request)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="client-default-request"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The DefaultRequest plugin allows you to configure default parameters for all requests.
</link-summary>

The [DefaultRequest](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-default-request/index.html) plugin allows you to configure default parameters for all [requests](request.md): specify a base URL, add headers, configure query parameters, and so on.


## Add dependencies {id="add_dependencies"}

`DefaultRequest` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.


## Install DefaultRequest {id="install_plugin"}

To install `DefaultRequest`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client) ...
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
//...
val client = HttpClient(CIO) {
    install(DefaultRequest)
}
```

... or call the `defaultRequest` function and [configure](#configure) required request parameters:

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
//...
val client = HttpClient(CIO) {
    defaultRequest {
        // this: DefaultRequestBuilder
    }
}
```

## Configure DefaultRequest {id="configure"}

### Base URL {id="url"}

`DefaultRequest` allows you to configure a base part of the URL that is merged with a [request URL](request.md#url).
For example, the `url` function below specifies a base URL for all requests:

```kotlin
defaultRequest {
    url("https://ktor.io/docs/")
}
```

If you make the following request using the client with the above configuration, ...

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" include-lines="25"}

... the resulting URL will be the following: `https://ktor.io/docs/welcome.html`.
To learn how base and request URLs are merged, see [DefaultRequest](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-default-request/index.html).


### URL parameters {id="url-params"}

The `url` function also allows you to specify URL components separately, for example:
- an HTTP scheme;
- a host name;
- a base URL path;
- a query parameter.

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" include-lines="15-20"}


### Headers {id="headers"}

To add a specific header to each request, use the `header` function:

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" include-lines="14,21-22"}

To avoid duplicating headers, you can use the `appendIfNameAbsent`, `appendIfNameAndValueAbsent`, and `contains` functions:

```kotlin
defaultRequest {
    headers.appendIfNameAbsent("X-Custom-Header", "Hello")
}
```

## Example {id="example"}

The example below uses the following `DefaultRequest` configuration:
* The `url` function defines an HTTP scheme, a host, a base URL path, and a query parameter.
* The `header` function adds a custom header to all requests.

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" include-lines="13-23"}

The request below made by this client specifies a latter path segment only and applies parameters configured for `DefaultRequest` automatically:

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" include-lines="25-26"}

You can find the full example here: [client-default-request](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-default-request).






