[//]: # (title: Bearer authentication in Ktor Client)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-auth</code>
</p>
<var name="example_name" value="client-auth-oauth-google"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>


Bearer authentication involves security tokens called bearer tokens. As an example, these tokens can be used as a part
of OAuth flow to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and
so on. You can learn how the OAuth flow might look from the [OAuth authorization flow](server-oauth.md#flow) section for
a Ktor server.

> On the server, Ktor provides the [Authentication](server-bearer-auth.md) plugin for handling bearer authentication.

## Configure bearer authentication {id="configure"}

A Ktor client allows you to configure a token to be sent in the `Authorization` header using the `Bearer` scheme. You can also specify the logic for refreshing a token if the old one is invalid. To configure the `bearer` provider, follow the steps below:

1. Call the `bearer` function inside the `install` block.
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
   
2. Configure how to obtain the initial access and refresh tokens using the `loadTokens` callback. This callback is intended to load cached tokens from a local storage and return them as the `BearerTokens` instance.

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
   
   The `abc123` access token is sent with each [request](client-requests.md) in the `Authorization` header using the `Bearer` scheme:
   ```HTTP
   GET http://localhost:8080/
   Authorization: Bearer abc123
   ```
   
3. Specify how to obtain a new token if the old one is invalid using `refreshTokens`.

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
   
   This callback works as follows:
   
   a. The client makes a request to a protected resource using an invalid access token and gets a `401` (Unauthorized) response.
     > If [several providers](client-auth.md#realm) are installed, a response should have the `WWW-Authenticate` header.
   
   b. The client calls `refreshTokens` automatically to obtain new tokens.

   c. The client makes one more request to a protected resource automatically using a new token this time.

4. (Optional) Specify a condition for sending credentials without waiting for the `401` (Unauthorized) response. For example, you can check whether a request is made to a specified host.

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

5. (Optional) Use the `cacheTokens` option to control whether bearer tokens are cached between requests. Disabling
   caching forces the client to reload tokens for every request, which can be useful when tokens change frequently:
   
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
   
    > For details on clearing cached credentials programmatically, see the general [Token caching and cache control](client-auth.md#token-caching)
    > section.

## Example: Using Bearer authentication to access Google API {id="example-oauth-google"}

Let's take a look at how to use bearer authentication to access Google APIs, which use the [OAuth 2.0 protocol](https://developers.google.com/identity/protocols/oauth2) for authentication and authorization. We'll investigate the [client-auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-auth-oauth-google) console application that gets Google's profile information. 

### Obtain client credentials {id="google-client-credentials"}
To access Google APIs, you first need OAuth client credentials:
1. Create or sign in to a Google account.
2. Open the [Google Cloud Console](https://console.cloud.google.com/apis/credentials) and create an `OAuth client ID` with the `Android` application type. This client
ID will be used to obtain an [authorization grant](#step1).

### OAuth authorization flow {id="oauth-flow"}

The OAuth authorization flow looks as follows:

```Console
(1)  --> [[[Authorization request|#step1]]]                Resource owner
(2)  <-- [[[Authorization grant (code)|#step2]]]           Resource owner
(3)  --> [[[Authorization grant (code)|#step3]]]           Authorization server
(4)  <-- [[[Access and refresh tokens|#step4]]]            Authorization server
(5)  --> [[[Request with valid token|#step5]]]             Resource server
(6)  <-- [[[Protected resource|#step6]]]                   Resource server
⌛⌛⌛    Token expired
(7)  --> [[[Request with expired token|#step7]]]           Resource server
(8)  <-- [[[401 Unauthorized response|#step8]]]            Resource server
(9)  --> [[[Authorization grant (refresh token)|#step9]]]  Authorization server
(10) <-- [[[Access and refresh tokens|#step10]]]            Authorization server
(11) --> [[[Request with new token|#step11]]]               Resource server
(12) <-- [[[Protected resource|#step12]]]                   Resource server
```
{disable-links="false"}

The next sections explain how each step is implemented and how the `Bearer` authentication provider assists in
accessing the API.

### (1) -> Authorization request {id="step1"}

The first step is to construct the authorization URL used to request the necessary permissions. This is done by appending
the required query parameters:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="23-31"}

- `client_id`: the client ID [obtained earlier](#google-client-credentials) used to access the Google APIs.
- `scope`: the scopes of resources required for a Ktor application. In this case, the application requests information about a user's profile.
- `response_type`: a grant type used to get an access token. In this case, it is set to `"code"` to obtain an authorization code.
- `redirect_uri`: the `http://127.0.0.1:8080` value indicates that the _Loopback IP address_ flow is used to get the authorization code.
   > To receive the authorization code using this URL, your application must be listening on the local web server.
   > For example, you can use a [Ktor server](server-create-and-configure.topic) to get the authorization code as a query parameter.
- `access_type`: set to `offline` so that the application can refresh access tokens when the user is not present at the browser.


### (2)  <- Authorization grant (code) {id="step2"}

Copy the authorization code from the browser, paste it in a console, and save it in a variable:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="32"}

### (3)  -> Authorization grant (code) {id="step3"}

Next, exchange the authorization code for tokens. To do this, create a client and install the
[ContentNegotiation](client-serialization.md) plugin with the `json` serializer. This serializer is required to deserialize tokens received
from the Google OAuth token endpoint.

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="38-41,65"}

Using the created client, you can securely pass the authorization code and other necessary options to the token
endpoint as [form parameters](client-requests.md#form_parameters):

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="68-77"}

As a result, the token endpoint sends tokens in a JSON object, which is deserialized to a `TokenInfo` class instance
using the installed `json` serializer. The `TokenInfo` class looks as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/TokenInfo.kt" include-lines="3-13"}

### (4)  <- Access and refresh tokens {id="step4"}

Once the tokens are received, store them so they can be supplied to the `loadTokens` and `refreshTokens` callbacks. In
this example, the storage is a mutable list of `BearerTokens`:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="35-36,78"}

> Note that `bearerTokenStorage` should be created before [initializing the client](#step3) since it will be used inside the client configuration.


### (5)  -> Request with valid token {id="step5"}

Now that valid tokens are available, the client can make a request to the protected Google API and retrieve user
information.

Before doing that, you need to adjust the client [configuration](#step3):

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="38-47,60-65"}

The following settings are specified: 

- The already installed [ContentNegotiation](client-serialization.md) plugin with the `json` serializer is required to deserialize user
information received from a resource server in a JSON format.

- The [Auth](client-auth.md) plugin with the `bearer` provider is configured as follows:
  * The `loadTokens` callback loads tokens from the [storage](#step4).
  * The `sendWithoutRequest` callback sends the access token without waiting for the `401 Unauthorized` response when
  accessing Google's protected API.

With this client, you can now make a request to the protected resource:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="81-96"}


### (6)  <- Protected resource {id="step6"}

The resource server returns information about a user in a JSON format. You can deserialize the response into the
`UserInfo` class instance and display a personal greeting:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="87-88"}


The `UserInfo` class looks as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/UserInfo.kt" include-lines="3-13"}


### (7)  -> Request with expired token {id="step7"}

At some point, the client repeats the request from [Step 5](#step5), but with an expired access token.

### (8)  <- 401 Unauthorized response {id="step8"}

When the token is no longer valid, the resource server returns a `401 Unauthorized` response. The client then invokes
the `refreshTokens` callback, which is responsible for obtaining new tokens.

> The `401` response returns JSON data with error details. This needs to be [handled when receiving a response](#step12).

### (9)  -> Authorization grant (refresh token) {id="step9"}

To obtain a new access token, you need to configure `refreshTokens` to make another request to the token endpoint. This
time, the `refresh_token` grant type is used instead of `authorization_code`:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="43-44,48-56,59,63-64"}

The `refreshTokens` callback uses `RefreshTokensParams` as a receiver and allows you to access the following settings:
- The `client` instance, which can be used to submit form parameters.
- The `oldTokens` property is used to access the refresh token and send it to the token endpoint.

> The `markAsRefreshTokenRequest` function exposed by `HttpRequestBuilder` enables special handling of requests used 
> to obtain a refresh token.

### (10) <- Access and refresh tokens {id="step10"}

After receiving new tokens, they need to be saved in the [token storage](#step4). With this, the `refreshTokens` callback
looks as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="48-59"}


### (11) -> Request with new token {id="step11"}

With the refreshed access token stored, the next request to the protected resource should succeed:
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="85"}

### (12) <-- Protected resource {id="step12"}

Given that the [401 response](#step8) returns JSON data with error details, update the example to read error responses
as an `ErrorInfo` object:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" include-lines="85-92"}

The `ErrorInfo` class is defined as follows:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/ErrorInfo.kt" include-lines="3-13"}

For the full example, see [client-auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-auth-oauth-google).





