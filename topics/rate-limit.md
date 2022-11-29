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

The `%plugin_name%` plugin allows you to limit the number of [requests](requests.md) a client can make within a certain time period.
Ktor allows you to configure rate limiting on different levels, for example:
- Enable a rate limiter globally for the entire application.
- Configure different rate limits for specific [routes](Routing_in_Ktor.md) only.
- Configure rate limiting based on specific request parameters: an IP address, an API key or access token, and so on.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>


## Configure %plugin_name% {id="configure"}

### Rate limiting overview {id="overview"}

Ktor uses the _token-bucket_ algorithm for rate limiting, which works as follows:
1. In the beginning, we have a bucket that is defined by its capacity - the number of tokens.
2. Each incoming request tries to consume one token from the bucket:
    - If there is enough capacity, the server handles a request and sends a response with the following headers:
        - `X-RateLimit-Limit`: a specified bucket capacity.
        - `X-RateLimit-Remaining`: the number of tokens remaining in a bucket.
        - `X-RateLimit-Reset`: a UTC timestamp (in seconds) that specifies the time of refilling a bucket.
    - If there is insufficient capacity, the server rejects a request with an HTTP `429 Too Many Requests` status and adds the `Retry-After` header, indicating how long the client should wait before making a follow-up request.
3. After a specified period of time, a bucket capacity is refilled.


### Register a rate limiter {id="register"}
Basic usage.
- Global or local: `global` or `register` inside the `install` block.
   ```kotlin
   install(RateLimit) {
       global {
           rateLimiter(limit = 5, refillPeriod = 60.seconds)
       }
   }
   ```

- `register` inside the `install` block.
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="14-17,33"}

### Configure rate limiting {id="configure-rate-limiting"}
1. Name (optional)
   ```kotlin
       install(RateLimit) {
           register(RateLimitName("protected")) {
               // ...
           }
       }
   ```

2. Rate limiter
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21-22,32"}

3. Request key (optional)
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21,23-25,32"}

4. Request weight (optional)
   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21,26-32"}
5. Modify response parameters (optional)
   ```kotlin
   register(RateLimitName("protected")) {
       modifyResponse { applicationCall, state ->
           applicationCall.response.header("X-RateLimit-Custom-Header", "Some value")
       }
   }
   ```


### Define rate limiting scope {id="rate-limiting-scope"}
- Use `rateLimit` inside routing (without or with a provider name)
- Get header values, request params, ...

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="40-46,60"}

Provider name:
```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="40,53-60"}

## Example {id="example"}

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt"}


