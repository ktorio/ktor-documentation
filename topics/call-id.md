[//]: # (title: CallId)

<var name="artifact_name" value="ktor-server-call-id"/>
<var name="plugin_name" value="CallId"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="call-id"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `%plugin_name%` plugin allows you to trace client requests end-to-end by using unique request or call IDs. Typically, working with call IDs in Ktor might look as follows:
1. A reverse proxy (such as Nginx) or cloud provider (such as [Heroku](heroku.md)) might add a call ID in a specific header, for example, `X-Request-Id`. In this case, `%plugin_name%` allows you to [retrieve](#retrieve) a call ID. Otherwise, if a request comes without a call ID, you can [generate](#generate) it.
2. Next, Ktor [verifies](#verify) a retrieved/generated call ID using a predefined dictionary. You can also provide your own condition to verify a call ID.
3. Finally, you can [send](#send) a call ID to the client.

Using this plugin along with [CallLogging](call-logging.md) helps you troubleshoot calls by [putting](#put-call-id-mdc) in the MDC context and configure a logger to show a call ID for each request.


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

### Retrieve a call ID {id="retrieve"}

Several ways:
* Retrieve from a header name:
   ```kotlin
   install(CallId) {
       retrieveFromHeader(HttpHeaders.XRequestId)
   }
   ```
   You can also use `header` to [retrieve a call ID and send it](#send) in the same header.

* Retrieve a call ID from an `ApplicationCall`:
   ```kotlin
   install(CallId) {
       retrieve { call ->
           call.request.header(HttpHeaders.XRequestId)
       }
   }
   ```

### Generate a call ID {id="generate"}
If a call ID is not available [](#retrieve), you can generate by setting length and dictionary:
```kotlin
install(CallId) {
    generate(10, "abcde12345")
}
```
or:
```kotlin
install(CallId) {
    val counter = atomic(0)
    generate {
        "generated-call-id-${counter.getAndIncrement()}"
    }
}
```


### Verify a call ID {id="verify"}

Default:
```kotlin
CALL_ID_DEFAULT_DICTIONARY: String = "abcdefghijklmnopqrstuvwxyz0123456789+/=-"
```

Custom less strict:

```kotlin
```
{src="snippets/call-id/src/main/kotlin/com/example/Application.kt" lines="12-15,17"}

Full example: [call-id](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/call-id).



### Send a call ID to the client {id="send"}

Retrieve from a header name:
```kotlin
install(CallId) {
    replyToHeader(HttpHeaders.XRequestId)
}
```

Or reply:
```kotlin
reply { call, callId ->
    call.response.header(HttpHeaders.XRequestId, callId)
}
```

Or you can use `header` to [retrieve a call ID and send it](#retrieve) in the same header: 

```kotlin
```
{src="snippets/call-id/src/main/kotlin/com/example/Application.kt" lines="12,16-17"}

Full example: [call-id](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/call-id).



## Put a call ID into MDC {id="put-call-id-mdc"}

The CallId plugin includes a `callIdMdc` extension method to be used when configuring the CallLogging.
It allows to associate the `callId` to the specified key to be put in the MDC context. 

```kotlin
```
{src="snippets/call-id/src/main/kotlin/com/example/Application.kt" lines="18-20"}

[Configure logback](logging.md#configure-logback):
```
```
{style="block" src="snippets/call-id/src/main/resources/logback.xml" lines="2-6"}

Full example: [call-id](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/call-id).