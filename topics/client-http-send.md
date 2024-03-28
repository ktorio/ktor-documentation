[//]: # (title: Intercepting requests using HttpSend)

<tldr>
<var name="example_name" value="client-http-send"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>


The [HttpSend](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-send/index.html) plugin allows you to monitor and retry HTTP calls depending on a response. For instance, you can implement call logging or retry a request if a server returns an error response (with the 4xx or 5xx status code).

The `HttpSend` plugin doesn't require installation. To use it, pass `HttpSend` to the `HttpClient.plugin` function and call the `intercept` method. The example below shows how to retry a request depending on the response status code:

```kotlin
```
{src="snippets/client-http-send/src/main/kotlin/com/example/Application.kt" include-lines="12-20"}

You can find the full sample here: [client-http-send](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-http-send).

