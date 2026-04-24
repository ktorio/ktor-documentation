[//]: # (title: Bearer authentication in Ktor Client)

<show-structure for="chapter" depth="3"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-auth</code>
</p>
<var name="example_name" value="client-auth-oauth-google"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Bearer authentication uses security tokens called _bearer tokens_. These tokens are commonly used in OAuth 2.0 flows to
authorize users through external providers, such as Google, Facebook, and X.

You can learn more about the OAuth process in the [OAuth authorization flow section of
the Ktor server documentation](server-oauth.md#flow).

> On the server, Ktor provides the [Authentication](server-bearer-auth.md) plugin for handling bearer authentication.

<include from="client-auth.md" element-id="add_dependencies"/>

## Configure bearer authentication {id="configure"}

A Ktor client allows you to send a token in the `Authorization` header using the `Bearer` scheme. You
can also define logic that refreshes tokens when they expire.

To configure bearer authentication, install the `Auth` plugin and configure the `bearer` provider:

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
//...
val client = HttpClient(CIO) {
   install(Auth) {
      bearer {
         // Configure bearer authentication
      }
   }
}
```

### Load tokens

Use the `loadTokens {}` callback to provide the initial access and refresh tokens. Typically, this callback loads cached
tokens from local storage and returns them as a `BearerTokens` instance.

```kotlin
install(Auth) {
   bearer {
       loadTokens {
           // Load tokens from a local storage and return them as the 'BearerTokens' instance
           BearerTokens("abc123", "xyz111")
       }
   }
}
```

In this example, the client sends the `abc123` access token in the `Authorization` header:

```HTTP
GET http://localhost:8080/
Authorization: Bearer abc123
```

### Refresh tokens

Use the `refreshTokens {}` callback to define how the client obtains new tokens when the current access token becomes
invalid:

```kotlin
install(Auth) {
   bearer {
       // Load tokens ...
       refreshTokens { // this: RefreshTokensParams
           // Refresh tokens and return them as the 'BearerTokens' instance
           BearerTokens("def456", "xyz111")
       }
   }
}
```
   
The refresh process works as follows:
   
1. The client makes a request to a protected resource using an invalid access token.
2. The resource server returns a `401 Unauthorized` response.
3. The client automatically invokes the `refreshTokens {}` callback to obtain new tokens.
4. The client retries the request to a protected resource using the new token.

When multiple requests fail with `401 Unauthorized` at the same time, the client performs the token refresh only once.
The first request that receives the `401` response triggers the `refreshTokens {}` callback. Other requests wait for the
refresh operation to complete and are then retried with the new token.

> If [several providers](client-auth.md#realm) are installed, a response should have the `WWW-Authenticate` header.
> If the client installs only one authentication provider, Ktor attempts that provider for `401 Unauthorized` responses
> even when the `WWW-Authenticate` header is missing or specifies a different scheme.
>
{style="tip"}

### Send credentials without waiting for 401

By default, the client sends credentials only after receiving a `401 Unauthorized` response.

You can override this behavior using the `sendWithoutRequest {}` callback function. This callback determines whether the
client should attach credentials before sending the request.

For example, the following configuration always sends the token when accessing Google APIs:

```kotlin
install(Auth) {
   bearer {
       // Load and refresh tokens ...
       sendWithoutRequest { request ->
           request.url.host == "www.googleapis.com"
       }
   }
}
```

### Cache tokens

Use the `cacheTokens` property to control whether bearer tokens are cached between requests.

If caching is disabled, the client calls the `loadTokens {}` function for every request:
   
```kotlin
install(Auth) {
    bearer {
        cacheTokens = false   // Reloads tokens for every request
        loadTokens {
            loadDynamicTokens()
        }
    }
}
```

Disabling caching can be useful when tokens change frequently.
   
> For details on clearing cached credentials programmatically, see the general [Token caching and cache control](client-auth.md#token-caching)
> documentation.
> 
{style="tip"}

## Example: Using Bearer authentication to access Google API {id="example-oauth-google"}

This example demonstrates how to use bearer authentication with Google APIs, which use the [OAuth 2.0 protocol](https://developers.google.com/identity/protocols/oauth2)
for authentication and authorization. 

The example application [client-auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-oauth-google) retrieves the user's Google profile information. 

### Obtain client credentials {id="google-client-credentials"}

To access Google APIs, you first need to obtain OAuth client credentials:

1. Create or sign in to a Google account.
2. Open the [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
3. Create an `OAuth client ID` with the `Android` application type. You will use this client
ID to obtain an [authorization grant](#step1).

### OAuth authorization flow {id="oauth-flow"}

The OAuth authorization flow consists of the following steps:

1. The client sends an [authorization request](#step1) to the resource owner.
2. The resource owner [returns an authorization code](#step2).
3. The client [sends the authorization code](#step3) to the authorization server.
4. The authorization server [returns access and refresh tokens](#step4).
5. The client [sends a request to the resource server using the access token](#step5).
6. The resource server [returns the protected resource](#step6).
7. After the access token expires, the client [sends a request with the expired token](#step7).
8. The resource server [responds with 401 Unauthorized](#step8).
9. The client [sends the refresh token](#step9) to the authorization server.
10. The authorization server [returns new access and refresh tokens](#step10).
11. The client [sends a new request to the resource server using the new access token](#step11).
12. The resource server [returns the protected resource](#step12).

The following sections explain how the Ktor client implements each step.

#### Authorization request {id="step1"}

First, construct the authorization URL used to request the necessary permissions. This is done by appending
the required query parameters:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="23-31"}

- `client_id`: the [OAuth client ID](#google-client-credentials) used to access the Google APIs.
- `scope`: the permissions requested by the application. In this case, it is information about a user's profile.
- `response_type`: a grant type used to get an access token. Set to `"code"` to obtain an authorization code.
- `redirect_uri`: the `http://127.0.0.1:8080` value indicates that the _Loopback IP address_ flow is used to get the authorization code.
   > To receive the authorization code using this URL, your application must be listening on the local web server.
   > For example, you can use a [Ktor server](server-create-and-configure.topic) to get the authorization code as a query parameter.
- `access_type`: set to `offline` so that the application can refresh access tokens when the user is not present at the browser.


#### Authorization grant (code) {id="step2"}

After granting access, the browser returns an authorization code. Copy the code and store it in a variable:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="32"}

#### Exchange authorization code for tokens {id="step3"}

Next, exchange the authorization code for tokens. To do this, create a client and install the
[`ContentNegotiation`](client-serialization.md) plugin with the JSON serializer:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="38-41,65"}

This serializer is required to deserialize tokens received from the Google OAuth token endpoint.

Using the created client, pass the authorization code and other necessary options to the token endpoint as
[form parameters](client-requests.md#form_parameters):

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="68-77"}

The token endpoint returns a JSON response that the client deserializes into a `TokenInfo` instance. The `TokenInfo`
class looks as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/TokenInfo.kt" include-lines="3-13"}

#### Store tokens {id="step4"}

Once the tokens are received, store them so they can be supplied to the `loadTokens {}` and `refreshTokens {}` callbacks. In
this example, the storage is a mutable list of `BearerTokens`:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="35-36,78"}

> Create the token storage before [initializing the client](#step3) because it will be used
> inside the client configuration.
>
{style="note"}

#### Send a request with a valid token {id="step5"}

Now that valid tokens are available, the client can make a request to the protected Google API and retrieve user
information.

Before doing that, configure the client to use bearer authentication:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="38-47,60-65"}

The following settings are specified: 

* The `loadTokens` callback retrieves tokens from [storage](#step4).
* The `sendWithoutRequest {}` callback sends the access token without waiting for the `401 Unauthorized` response when
  calling the Google API.

With this client, you can now make a request to the protected resource:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="81-96"}


#### Access the protected resource {id="step6"}

The resource server returns information about a user in a JSON format. You can deserialize the response into the
`UserInfo` class instance and display a personal greeting:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="87-88"}


The `UserInfo` class looks as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/UserInfo.kt" include-lines="3-13"}


#### Request with expired token {id="step7"}

At some point, the client repeats the request from [Step 5](#step5), but with an expired access token.

#### 401 Unauthorized response {id="step8"}

When the token is no longer valid, the resource server returns a `401 Unauthorized` response. The client then invokes
the `refreshTokens {}` callback, which is responsible for obtaining new tokens.

> The `401 Unauthorized` response returns JSON data with error details. This needs to be [handled when receiving a response](#step12).
>
{style="tip"}

#### Refresh the access token {id="step9"}

To obtain a new access token, configure the `refreshTokens {}` callback to make another request to the token endpoint.
This time, use the `refresh_token` grant type instead of `authorization_code`:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="43-44,48-56,59,63-64"}

The `refreshTokens {}` callback uses `RefreshTokensParams` as a receiver and allows you to access the following settings:
* The `client` instance, which can be used to submit form parameters.
* The `oldTokens` property is used to access the refresh token and send it to the token endpoint.
* The `.markAsRefreshTokenRequest()` function exposed by `HttpRequestBuilder` marks the request for refreshing auth 
  tokens, resulting in a special handling of it.


#### Save refreshed tokens {id="step10"}

After receiving new tokens, save them in the [token storage](#step4). With this, the `refreshTokens {}` callback
looks as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="48-59"}


#### Request with new token {id="step11"}

With the refreshed access token stored, the next request to the protected resource should succeed:
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="85"}

#### Handle API errors {id="step12"}

Given that the [`401 Unauthorized` response](#step8) returns JSON data with error details, update the example to read
error responses as an `ErrorInfo` object:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="85-92"}

The `ErrorInfo` class is defined as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/ErrorInfo.kt" include-lines="3-13"}

> For the full example, see [client-auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-oauth-google).
> 
{style="tip"}




