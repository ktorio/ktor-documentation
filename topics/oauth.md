[//]: # (title: OAuth)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-auth</code>
</p>
<var name="example_name" value="auth-oauth-google"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

[OAuth](https://oauth.net/) is an open standard for access delegation. OAuth can be used to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and so on.

The `oauth` provider supports the authorization code flow. You can configure OAuth parameters in one place, and Ktor will automatically make a request to a specified authorization server with the necessary parameters. 

> You can get general information about authentication and authorization in Ktor in the [](authentication.md) section.

## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="OAuth"/>
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## OAuth authorization flow {id="flow"}
The OAuth authorization flow in a Ktor application might look as follows:
1. A user opens a login page in a Ktor application.
2. Ktor makes an automatic redirect to the authorization page for a specific provider and passes the necessary parameters:
   * a client ID used to access APIs of the selected provider;
   * a callback or redirect URL specifying a Ktor application page that will be opened after authorization is completed;
   * scopes of third-party resources required for a Ktor application;
   * a grant type used to get an access token (Authorization Code in our case).
3. The authorization page shows a consent screen with the level of permissions required for a Ktor application. These permissions depend on scopes passed in step 2. 
4. If a user approves the requested permissions, the authorization server redirects back to a redirect URL and sends the authorization code.
5. Ktor makes one more automatic request to the specified access token URL and passes the following parameters:
   * an authorization code;
   * a client ID and client secret.
   
   The authorization server returns an access token.
6. A client can now use a token to make a request to the required service of the selected provider. In most cases, a token will be sent in the `Authorization` header using the `Bearer` schema.
7. A service validates a token, uses its scope for authorization, and returns the requested data.


## Install OAuth {id="install"}
To install the `oauth` authentication provider, call [oauth](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/oauth.html) function inside the `install` block:

```kotlin
install(Authentication) {
   oauth {
        // Configure oauth authentication
    }
}
```
You can optionally specify a [provider name](authentication.md#provider-name).


## Configure OAuth {id="configure-oauth"}

This section demonstrates how to configure the `oauth` provider for authorizing users of your application using Google. You can find the complete runnable example here: [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-oauth-google). 

> Note that to access Google APIs you need to create authorization credentials in the [Google Cloud Console](https://console.cloud.google.com/apis/credentials). Credentials contain the client ID and client secret that will be used to configure the `oauth` provider.


### Step 1: Create the HTTP client {id="create-http-client"}

Before configuring the `oauth` provider, you need to initialize the `HttpClient` that will be used by the server to make requests to the OAuth server. You can learn how to add the `HttpClient` dependencies and configure it from [](client.md).

```kotlin
```
{src="snippets/auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="22-26"}

Note that the client also has the [](json.md) plugin installed. This is required to deserialize received JSON data [after a request to API](#request-api).


### Step 2: Configure the OAuth provider {id="configure-oauth-provider"}

The code snippet below shows how to create and configure the `oauth` provider with the `auth-oauth-google` name.

```kotlin
```
{src="snippets/auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="27-43"}

* The `urlProvider` specifies a [redirect route](#redirect-route) that will be opened when authorization is completed. 
* `providerLookup` allows you to specify OAuth settings for a required provider. These settings allow Ktor to make automatic requests to the OAuth server. 
* The `client` property specifies the [HttpClient](#create-http-client) used by Ktor to make requests to the OAuth server.


### Step 3: Add a login route {id="login-route"}
After configuring the `oauth` provider, you need to create a protected login route inside the `authenticate` function that accepts the name of the `oauth` provider. When Ktor receives a request to this route, it will be automatically redirected to `authorizeUrl` defined in [providerLookup](#configure-oauth-provider).

```kotlin
```
{src="snippets/auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="44-48,55,78"}

A user will see the authorization page with the level of permissions required for a Ktor application. These permissions depend on `defaultScopes` specified in [providerLookup](#configure-oauth-provider).

### Step 4: Add a redirect route {id="redirect-route"}

Apart from the login route, you need to create a redirect route that will be invoked after authorization is completed. The address of this route was specified using the [urlProvider](#configure-oauth-provider) property.

Inside this route you can retrieve the [OAuthAccessTokenResponse](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-o-auth-access-token-response/index.html) object using the [call.principal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/principal.html) function. `OAuthAccessTokenResponse` allows you to access a token and other parameters returned by the OAuth server.

```kotlin
```
{src="snippets/auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="44-55,78"}

In this example, the following actions are performed after receiving a token:
* a token is saved to a cookie [session](sessions.md), whose content can be accessed inside other routes;
* a user is redirected to the next route where a request to Google API is made.


### Step 5: Make a request to API {id="request-api"}

After receiving a token inside the [redirect route](#redirect-route) and saving it to a session, you can make the request to external APIs using this token. The code snippet below shows how to use the [HttpClient](#create-http-client) to make such a request and get a user's information by sending this token in the `Authorization` header.

```kotlin
```
{src="snippets/auth-oauth-google/src/main/kotlin/com/example/Application.kt" lines="65-77"}

You can find the complete runnable example here: [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-oauth-google). 
