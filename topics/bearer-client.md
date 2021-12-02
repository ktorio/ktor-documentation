[//]: # (title: Bearer authentication)

<microformat>
<var name="example_name" value="client-auth-oauth-google"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>


Bearer authentication involves security tokens called bearer tokens. As an example, these tokens can be used as a part of OAuth flow to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and so on. You can learn how the OAuth flow might look from the [OAuth authorization flow](oauth.md#flow) section for a Ktor server.

## Configure bearer authentication {id="configure"}

A Ktor client allows you to configure a token to be sent in the `Authorization` header using the `Bearer` scheme. You can also specify logic for refreshing a token if the old one is invalid. To configure the `bearer` provider, follow the steps below:

1. Call the `bearer` function inside the `install` block.
   ```kotlin
   val client = HttpClient(CIO) {
       install(Auth) {
          bearer {
             // Configure bearer authentication
          }
       }
   }
   ```
   
2. Configure how to obtain tokens: 
   - Use the `loadTokens` callback to load a cached token from a local storage.
   - Specify how to obtain a new token if the old one is invalid using `refreshTokens`. Note that this block will be called after receiving a `401` (Unauthorized) response with the `WWW-Authenticate` header.
   
   Both callbacks should return tokens as the `BearerTokens` instance.

   ```kotlin
   install(Auth) {
       bearer {
           loadTokens {
               // Load tokens from a local storage and return them as the BearerTokens instance
           }
           refreshTokens {
               // Refresh tokens and return them as the BearerTokens instance
           }
       }
   }
   ```




## Example: Using OAuth to access Google API {id="example-oauth-google"}

OAuth authorization flow overview:

```Console
(1)  --> [[[Authorization request|#step1]]]                Resource owner
(2)  <-- Authorization grant (code)           Resource owner
(3)  --> Authorization grant (code)           Authorization server
(4)  <-- Access and refresh tokens            Authorization server
(5)  --> Request with valid token             Resource server
(6)  <-- Protected resource                   Resource server
⌛⌛⌛    Token expired
(7)  --> Request with expired token           Resource server
(8)  <-- 401 Unauthorized response            Resource server
(9)  --> Authorization grant (refresh token)  Authorization server
(10) <-- Access and refresh tokens            Authorization server
(11) --> Request with new token               Resource server
(12) <-- Protected resource                   Resource server
```
{disable-links="false"}

- `loadTokens` - steps 5-6
- `refreshTokens` - steps 7-12

### (1) -> Authorization request {id="step1"}

A user opens a login page in a Ktor application.
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="20-28"}

### (2)  <- Authorization grant (code) {id="step2"}
Receive the authorization code, paste it in a console, and save it.
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="29"}

### (3)  -> Authorization grant (code) {id="step3"}
Exchange the authorization code for tokens
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="32-45"}

The `TokenInfo` class:
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/TokenInfo.kt" lines="3-13"}

### (4)  <- Access and refresh tokens {id="step4"}

Save tokens:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="46-47"}


### (5)  -> Request with valid token {id="step5"}

Configure the client (expectSuccess, json, and auth):

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="50-59,72-74"}

Make a request. The client adds the bearer token automatically.

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="81"}



### (6)  <- Protected resource {id="step6"}

Response:

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="83-84"}


The `UserInfo` class:
```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/models/UserInfo.kt" lines="3-13"}


### (7)  -> Request with expired token {id="step7"}

As for [Step 5](#step5)

### (8)  <- 401 Unauthorized response {id="step8"}

`401` Unauthorized response

### (9)  -> Authorization grant (refresh token) {id="step9"}

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="55-56,60-68,71-73"}

### (10) <- Access and refresh tokens {id="step10"}

See [Step 4](#step4)

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="60-71"}


### (11) -> Request with new token {id="step11"}

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="81"}

### (12) <-- Protected resource {id="step12"}

```kotlin
```
{src="snippets/client-auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="81-88"}





