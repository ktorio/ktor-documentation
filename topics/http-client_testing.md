[//]: # (title: Testing)

<include src="lib.xml" include-id="outdated_warning"/>

Ktor exposes a `MockEngine` for the HttpClient. This engine allows simulating HTTP calls without actually connecting to the endpoint. It allows to set a code block, that can handle the request and generates a response.

## Add dependencies {id="add_dependencies"}
To use `MockEngine`, you need to include the `ktor-client-mock` artifact in the build script:

<var name="artifact_name" value="ktor-client-mock"/>
<include src="lib.xml" include-id="add_ktor_artifact_testing"/>


## Usage {id="usage"}

The usage is straightforward: the MockEngine class has a method `addHandler` in `MockEngineConfig`, that receives a block/callback that will handle the request. This callback receives an `HttpRequest` as a parameter, and must return a `HttpResponseData`. There are many helper methods to construct the response.

Full API description and list of helper methods could be found [here](https://api.ktor.io/ktor-client/ktor-client-mock/ktor-client-mock/io.ktor.client.engine.mock/index.html).

A sample illustrating this:

```kotlin
val client = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            when (request.url.fullUrl) {
                "https://example.org/" -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Text.Plain.toString()))
                    respond("Hello, world", headers = responseHeaders)
                }
                else -> error("Unhandled ${request.url.fullUrl}")
            }
        }
    }
}

private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"
```