[//]: # (title: Rate limiting)

<show-structure for="chapter" depth="3"/>
<primary-label ref="server-plugin"/>

<var name="plugin_name" value="RateLimit"/>
<var name="package_name" value="io.ktor.server.plugins.ratelimit"/>
<var name="artifact_name" value="ktor-server-rate-limit"/>
<var name="plugin_api_link" value="https://api.ktor.io/ktor-server-rate-limit/io.ktor.server.plugins.ratelimit/-rate-limit.html"/>

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

The [`%plugin_name%`](%plugin_api_link%) plugin allows you to limit the number of [requests](server-requests.md) 
a client can make within a specified time period.

Ktor provides several ways to configure rate limiting:

* Apply a rate limit globally to the entire application or configure different limits for specific [resources](server-routing.md).
* Apply rate limits based on request parameters, such as an IP address, API key or access token.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Configure %plugin_name% {id="configure"}

### Overview {id="overview"}

Ktor uses the _token bucket_ algorithm for rate limiting, which works as follows:
1. A bucket is created with a specified capacity, which defines the number of available tokens.
2. Each incoming request consumes one token from the bucket:
   * If there is enough capacity, the server processes the request and includes the following headers in the response:
     * `X-RateLimit-Limit`: the bucket capacity.
     * `X-RateLimit-Remaining`: the number of tokens remaining in the bucket.
     * `X-RateLimit-Reset`: the UTC timestamp, in seconds, that specifies when the bucket is refilled.
   * If there is insufficient capacity, the server rejects a request using a `429 Too Many Requests` response. The response
     includes the `Retry-After` header, indicating how many seconds the client should wait before sending another request.
3. After the specified refill period, the bucket is refilled.

### Register a rate limiter {id="register"}

You can apply rate limiting globally to the entire application or register a rate limiter for specific routes:

* To apply rate limiting globally, call the `global()` function and configure the rate limiter:

   ```kotlin
   install(RateLimit) {
       global {
           rateLimiter(limit = 5, refillPeriod = 60.seconds)
       }
   }
   ```

* To configure rate limiting for specific routes, use the `register()` function to register a rate limiter:

   ```kotlin
   ```
   {src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="14-17,33"}

The examples above show the minimal configuration required for the `%plugin_name%` plugin.
If you use `register()`, you also need to apply the registered rate limiter to a [specific route](#rate-limiting-scope).


### Configure rate limiting {id="configure-rate-limiting"}

You can configure a rate limiter using the options below.

#### Name a rate limiter

Use the `register()` function to assign a name to a rate limiter. You can then apply the named rate limiter to
[specific routes](#rate-limiting-scope):

```kotlin
    install(RateLimit) {
        register(RateLimitName("protected")) {
            // ...
        }
    }
```

#### Set the limit and refill period

Use the `rateLimiter()` function to configure the bucket capacity and refill period:

* `limit` specifies the number of available tokens.
* `refillPeriod` specifies how often the bucket is refilled.

The following example allows up to 30 requests per minute:

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21-22,32"}

#### Distinguish requests by key

Use the `requestKey()` function to return a key for each request. Requests with different keys have independent
rate limits.

The following example uses the `login` [query parameter](server-requests.md#query_parameters) to distinguish between users:

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21,23-25,32"}

> Ensure that request keys have appropriate `equals` and `hashCode` implementations.
> 
{style="tip"}

#### Rate limit authenticated users

You can use an authentication principal as a request key to apply rate limits per authenticated user.

Nest `rateLimit()` inside `authenticate()`, then access the principal from `requestKey()`:

```kotlin
install(Authentication) {
    basic("auth") { validate { UserIdPrincipal(it.name) } }
}
install(RateLimit) {
    register(RateLimitName("per-user")) {
        rateLimiter(limit = 10, refillPeriod = 60.seconds)
        requestKey { call.principal<UserIdPrincipal>()?.name ?: "anonymous" }
    }
}

routing {
    authenticate("auth") {
        rateLimit(RateLimitName("per-user")) {
            get("/api") { call.respondText("OK") }
        }
    }
}
```

#### Set the request weight

Use the `requestWeight()` function to specify how many tokens each request consumes. The function receives the
application call and the request key.

In the following example, requests with the `jetbrains` key consume one token, while all other requests consume two:

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="21,23-32"}

#### Customize the response

Use the `modifyResponse()` function to customize the response when rate limiting is applied.

For example, you can add custom rate-limit headers:

```kotlin
register(RateLimitName("protected")) {
    modifyResponse { applicationCall, state ->
        applicationCall.response.header("X-RateLimit-Custom-Header", "Some value")
    }
}
```

### Define rate limiting scope {id="rate-limiting-scope"}

After configuring a rate limiter, you can use the `rateLimit()` function to apply it to specific routes.

#### Apply the default rate limiter

Use the `rateLimit()` function without a name to apply the default registered rate limiter:

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="40-46,60"}

#### Apply a named rate limiter

Pass a `RateLimitName` to the `rateLimit()` function to apply a [named rate limiter](#configure-rate-limiting):

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt" include-lines="40,53-60"}

## Example {id="example"}

The following example shows how to apply different rate limiters to different routes.
It configures:

* A default rate limiter for the home page.
* A named public rate limiter for the public API.
* A named protected rate limiter that uses request keys and weights.
* The [`StatusPages`](server-status-pages.md) plugin to customize responses for requests rejected with a `429 Too Many Requests` response.

```kotlin
```
{src="snippets/rate-limit/src/main/kotlin/com/example/Application.kt"}

> For the full example, see [rate-limit](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/rate-limit).
>
{style="tip"}