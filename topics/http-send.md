[//]: # (title: Intercepting requests using HttpSend)

<microformat>
<var name="example_name" value="client-http-send"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>


The `HttpSend` plugin allows you to monitor and retry HTTP calls depending on a response. For instance, you can implement call logging or retry a request if a server returns an error response (with the 4xx or 5xx status code).

The `HttpSend` plugin doesn't require installation. To use it, pass `HttpSend` to the `HttpClient.plugin` function and call the `intercept` method. The example below shows how to retry a request depending on the response status code:

```kotlin
```
{src="snippets/client-http-send/src/main/kotlin/com/example/Application.kt" lines="12-20"}

You can find the full sample here: [client-http-send](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-http-send).

