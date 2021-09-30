[//]: # (title: Default request)

<microformat>
<var name="example_name" value="client-default-request"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `DefaultRequest` plugin allows you to configure default parameters for all [requests](request.md).


## Add dependencies {id="add_dependencies"}

`DefaultRequest` only requires the [ktor-client-core](client.md#client-dependency) artifact and doesn't need any specific dependencies.


## Install DefaultRequest {id="install_plugin"}

To install `DefaultRequest`, pass it to the `install` function inside a [client configuration block](client.md#configure-client) ...
```kotlin
val client = HttpClient(CIO) {
    install(DefaultRequest)
}
```

... or call the `defaultRequest` function and [configure](#configure) required request parameters:

```kotlin
val client = HttpClient(CIO) {
    defaultRequest {
        // this: HttpRequestBuilder
    }
}
```

## Configure DefaultRequest {id="configure"}

The example below uses the following `DefaultRequest` configuration:
* The `method` property specifies the `GET` as the default HTTP method.
* The `host` and `port` properties specify the URL's host and port.
* The `url` function is used to define `HTTPS` as a default scheme.
* The `header` function adds a custom header to all requests.

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" lines="15-24,34"}

The request below made by this client specifies a request path only and applies parameters configured for `DefaultRequest` automatically:

```kotlin
```
{src="snippets/client-default-request/src/main/kotlin/com/example/Application.kt" lines="36-38"}

You can find the full example here: [client-default-request](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-default-request).
