[//]: # (title: Rate limiting)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="RateLimit"/>
<var name="package_name" value="io.ktor.server.plugins.ratelimit"/>
<var name="artifact_name" value="ktor-server-rate-limit"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="rate-limit"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
%plugin_name% provides the ability to validate a body of incoming requests.
</link-summary>

The `%plugin_name%` plugin allows you to limit the number of [requests](requests.md) 
a client can make within a certain time period.
Ktor provides different means for configuring rate limiting, for example:
- You can enable rate limiting globally for a whole application or configure different rate limits for different [resources](Routing_in_Ktor.md).
- You can configure rate limiting based on specific request parameters: an IP address, an API key or access token, and so on.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

### Overview {id="overview"}

Ktor uses the _token bucket_ algorithm for rate limiting, which works as follows:
1. In the beginning, we have a bucket defined by its capacity - the number of tokens.
2. Each incoming request tries to consume one token from the bucket:
    - If there is enough capacity, the server handles a request and sends a response with the following headers:
        - `X-RateLimit-Limit`: a specified bucket capacity.
        - `X-RateLimit-Remaining`: the number of tokens remaining in a bucket.
        - `X-RateLimit-Reset`: a UTC timestamp (in seconds) that specifies the time of refilling a bucket.
    - If there is insufficient capacity, the server rejects a request using a `429 Too Many Requests` response and adds the `Retry-After` header, indicating how long the client should wait before making a follow-up request.
3. After a specified period of time, a bucket capacity is refilled.


### Register a rate limiter {id="register"}
Ktor allows you to apply rate limiting globally to a whole application or to specific routes:
- To apply rate limiting to a whole application, call the `global` method and pass a configured rate limiter.
   ```kotlin
   install(RateLimit) {
       global {
           rateLimiter(limit = 5, refillPeriod = 60.seconds)
       }
   }
   ```

- The `register` method registers a rate limiter that can be applied to specific routes.
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="14-17,33"}

Code samples above demonstrate minimal configurations for the `%plugin_name%` plugin, 
but for a rate limiter registered using the `register` method you also need to apply it to a [specific route](#rate-limiting-scope).


### Configure rate limiting {id="configure-rate-limiting"}

In this section, we'll see how to configure rate limiting:

1. (Optional) The `register` method allows you to specify a rate limiter name that can be used to
   apply rate limiting rules to [specific routes](#rate-limiting-scope):
   ```kotlin
       install(RateLimit) {
           register(RateLimitName("protected")) {
               // ...
           }
       }
   ```

2. The `rateLimiter` method creates a rate limiter with two parameters: 
   `limit` defines the bucket capacity, while `refillPeriod` specifies a refill period for this bucket.
   A rate limiter in the example below allows handling 30 requests per minute:
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21-22,32"}

3. (Optional) `requestKey` allows you to specify a function that returns a key for a request.
   Requests with different keys have independent rate limits.
   In the example below, the `login` [query parameter](requests.md#query_parameters) is a key 
   used to distinguish different users:
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21,23-25,32"}

4. (Optional) `requestWeight` sets a function that returns how many tokens are consumed by a request.
   In the example below, a request key is used to configure a request weight:
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21,23-32"}

5. (Optional) `modifyResponse` allows you to override default `X-RateLimit-*` headers sent with each request:
   ```kotlin
   register(RateLimitName("protected")) {
       modifyResponse { applicationCall, state ->
           applicationCall.response.header("X-RateLimit-Custom-Header", "Some value")
       }
   }
   ```


### Define rate limiting scope {id="rate-limiting-scope"}

After configuring a rate limiter, you can apply its rules to specific routes using the `rateLimit` method:

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="40-46,60"}

This method can also accept a [rate limiter name](#configure-rate-limiting):

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="40,53-60"}


## Example {id="example"}

The code sample below demonstrates how to use the `RateLimit` plugin to apply different rate limiters to different resources.
The [StatusPages](status_pages.md) plugin is used to handle rejected requests,
for which the `429 Too Many Requests` response was sent.

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt"}


You can find the full example here: [rate-limit](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/rate-limit).
