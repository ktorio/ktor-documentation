[//]: # (title: CallId)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-call-id"/>
<var name="package_name" value="io.ktor.server.plugins.callid"/>
<var name="plugin_name" value="CallId"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="call-id"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-call-id/io.ktor.server.plugins.callid/-call-id.html) plugin allows you to trace client requests end-to-end by using unique request IDs or call IDs. Typically, working with a call ID in Ktor might look as follows:
1. First, you need to obtain a call ID for a specific request in one of the following ways:
   * A reverse proxy (such as Nginx) or cloud provider (such as [Heroku](heroku.md)) might add a call ID in a specific header, for example, `X-Request-Id`. In this case, Ktor allows you to [retrieve](#retrieve) a call ID.
   * Otherwise, if a request comes without a call ID, you can [generate](#generate) it on the Ktor server.
2. Next, Ktor [verifies](#verify) a retrieved/generated call ID using a predefined dictionary. You can also provide your own condition to verify a call ID.
3. Finally, you can [send](#send) a call ID to the client in a specific header, for example, `X-Request-Id`.

Using `%plugin_name%` along with [CallLogging](call-logging.md) helps you troubleshoot calls by [putting a call ID](#put-call-id-mdc) in the MDC context and configuring a logger to show a call ID for each request.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

### Retrieve a call ID {id="retrieve"}

`%plugin_name%` provides several ways to retrieve a call ID:

* To retrieve a call ID from the specified header, use the `retrieveFromHeader` function, for example:
   ```kotlin
   install(CallId) {
       retrieveFromHeader(HttpHeaders.XRequestId)
   }
   ```
   You can also use the `header` function to [retrieve and send a call ID](#send) in the same header.

* If required, you can retrieve a call ID from the `ApplicationCall`:
   ```kotlin
   install(CallId) {
       retrieve { call ->
           call.request.header(HttpHeaders.XRequestId)
       }
   }
   ```
Note that all retrieved call IDs are [verified](#verify) using a default dictionary.

### Generate a call ID {id="generate"}

If an incoming request doesn't include a call ID, you can generate it using the `generate` function:
* The example below shows how to generate a call ID with a specific length from the predefined dictionary:
   ```kotlin
   install(CallId) {
       generate(10, "abcde12345")
   }
   ```
* In the example below, the `generate` function accepts a block for generating a call ID:
   ```kotlin
   install(CallId) {
       val counter = atomic(0)
       generate {
           "generated-call-id-${counter.getAndIncrement()}"
       }
   }
   ```


### Verify a call ID {id="verify"}

All [retrieved](#retrieve)/[generated](#generate) call IDs are verified using a default dictionary, which looks as follows:

```kotlin
CALL_ID_DEFAULT_DICTIONARY: String = "abcdefghijklmnopqrstuvwxyz0123456789+/=-"
```
This means that call IDs containing capital letters won't pass verification. If required, you can apply less strict rules by using the `verify` function:

```kotlin
```
{src="snippets/call-id/src/main/kotlin/com/example/Application.kt" include-lines="13,15-18"}

You can find the full example here: [call-id](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/call-id).



### Send a call ID to the client {id="send"}

After [retrieving](#retrieve)/[generating](#generate) a call ID, you can send it to the client:

* The `header` function can be used to [retrieve a call ID](#retrieve) and send it in the same header:

   ```kotlin
   ```
  {src="snippets/call-id/src/main/kotlin/com/example/Application.kt" include-lines="13-14,18"}

  You can find the full example here: [call-id](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/call-id).

* The `replyToHeader` function sends a call ID in the specified header:
   ```kotlin
   install(CallId) {
       replyToHeader(HttpHeaders.XRequestId)
   }
   ```

* If required, you can use `ApplicationCall` to send a call ID in a [response](responses.md):
   ```kotlin
   reply { call, callId ->
       call.response.header(HttpHeaders.XRequestId, callId)
   }
   ```


## Put a call ID into MDC {id="put-call-id-mdc"}

Using `%plugin_name%` along with [CallLogging](call-logging.md) helps you troubleshoot calls by putting a call ID in the MDC context and configuring a logger to show a call ID for each request. To do this, call the `callIdMdc` function inside the `CallLogging` configuration block and specify the desired key to be put in the MDC context:

```kotlin
```
{src="snippets/call-id/src/main/kotlin/com/example/Application.kt" include-lines="19-21"}

This key can be passed to a [logger configuration](logging.md#configure-logger) to show call IDs in the log. For instance, the `logback.xml` file might look as follows:
```
```
{style="block" src="snippets/call-id/src/main/resources/logback.xml" include-lines="2-6"}

You can find the full example here: [call-id](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/call-id).