[//]: # (title: OAuth)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>

<var name="plugin_name" value="OAuth"/>
<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="auth-oauth-google"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

[OAuth](https://oauth.net/) is an open standard for access delegation. OAuth can be used to authorize users of your
application by using external providers, such as Google, Facebook, Twitter, and so on.

The `oauth` provider supports the authorization code flow. You can configure OAuth parameters in one place, and Ktor
will automatically make a request to a specified authorization server with the necessary parameters.

> You can get general information about authentication and authorization in Ktor in the [](server-auth.md) section.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install the Sessions plugin

To avoid requesting authorization every time a client tries to access a protected resource, you can store the access
token in the session upon successful authorization.
You can then retrieve the access token from the current session within the handler of the protected route and use it to
request the resource.

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="14,29-33,115,141-143"}

## OAuth authorization flow {id="flow"}

The OAuth authorization flow in a Ktor application might look as follows:

1. A user opens a login page in a Ktor application.
2. Ktor makes an automatic redirect to the authorization page for a specific provider and passes the
   necessary [parameters](#configure-oauth-provider):
    * A client ID used to access APIs of the selected provider.
    * A callback or redirect URL specifying a Ktor application page that will be opened after authorization is
      completed.
    * Scopes of third-party resources required for a Ktor application.
    * A grant type used to get an access token (Authorization Code).
    * A `state` parameter used to mitigate CSRF attacks and redirect users.
    * Optional parameters specific for a certain provider.
3. The authorization page shows a consent screen with the level of permissions required for a Ktor application. These
   permissions depend on the specified scopes, as configured in [](#configure-oauth-provider).
4. If a user approves the requested permissions, the authorization server redirects back to the designated redirect URL
   and sends the authorization code.
5. Ktor makes one more automatic request to the specified access token URL, including the following parameters:
    * Authorization code.
    * Client ID and client secret.

   The authorization server responds by returning an access token.
6. The client can then use this token to make a request to the required service of the selected provider. In most cases,
   a token will be sent in the `Authorization` header using the `Bearer` schema.
7. The service validates the token, uses its scope for authorization, and returns the requested data.

## Install OAuth {id="install"}

To install the `oauth` authentication provider, call
the [oauth](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/oauth.html)
function inside the `install` block. Optionally, you can [specify a provider name](server-auth.md#provider-name).
For example, to install an `oauth` provider with the name "auth-oauth-google" it would look like the following:

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="9-10,29-30,35-38,65-66,115"}

## Configure OAuth {id="configure-oauth"}

This section demonstrates how to configure the `oauth` provider for authorizing users of your application using Google.
For the complete runnable example,
see [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-oauth-google).

### Prerequisites: Create authorization credentials {id="authorization-credentials"}

To access Google APIs, you need to create authorization credentials in the Google Cloud Console.

1. Open the [Credentials](https://console.cloud.google.com/apis/credentials) page in the Google Cloud Console.
2. Click **CREATE CREDENTIALS** and choose `OAuth client ID`.
3. Choose `Web application` from the dropdown.
4. Specify the following settings:
    * **Authorised JavaScript origins**: `http://localhost:8080`.
    * **Authorised redirect URIs**: `http://localhost:8080/callback`.
      In Ktor, the [urlProvider](#configure-oauth-provider) property is used to specify a redirect route that will be
      opened when authorization is completed.

5. Click **CREATE**.
6. In the invoked dialog, copy the created client ID and client secret that will be used to configure the `oauth`
   provider.

### Step 1: Create the HTTP client {id="create-http-client"}

Before configuring the `oauth` provider, you need to create the [HttpClient](client-create-and-configure.md) that will be used by the
server to make requests to the OAuth server. The [ContentNegotiation](client-serialization.md) client plugin with the
JSON serializer is required to deserialize received JSON data [after a request to the API](#request-api).

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="22-28"}

The client instance is passed to the `main` [module function](server-modules.md) to have the capability to create a separate
client instance in a server [test](server-testing.md).

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="30,115"}

### Step 2: Configure the OAuth provider {id="configure-oauth-provider"}

The code snippet below shows how to create and configure the `oauth` provider with the `auth-oauth-google` name.

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="34-66"}

* The `urlProvider` specifies a [redirect route](#redirect-route) that will be invoked when authorization is completed.
  > Make sure that this route is added to a list of [**Authorised redirect URIs**](#authorization-credentials).
* `providerLookup` allows you to specify OAuth settings for a required provider. These settings are represented by
  the [OAuthServerSettings](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-o-auth-server-settings/index.html)
  class and allow Ktor to make automatic requests to the OAuth server.
* The `fallback` property handles OAuth flow errors by responding with a redirect or custom response.
* The `client` property specifies the [HttpClient](#create-http-client) used by Ktor to make requests to the OAuth
  server.

### Step 3: Add a login route {id="login-route"}

After configuring the `oauth` provider, you need
to [create a protected login route](server-auth.md#authenticate-route) inside the `authenticate` function that
accepts the name of the `oauth` provider. When Ktor receives a request to this route, it will be automatically
redirected to `authorizeUrl` defined
in [providerLookup](#configure-oauth-provider).

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="67-71,87,114"}

A user will see the authorization page with the level of permissions required for a Ktor application. These permissions
depend on `defaultScopes` specified in [providerLookup](#configure-oauth-provider).

### Step 4: Add a redirect route {id="redirect-route"}

Apart from the login route, you need to create the redirect route for the `urlProvider`, as specified
in [](#configure-oauth-provider).

Inside this route you can retrieve
the [OAuthAccessTokenResponse](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-o-auth-access-token-response/index.html)
object using the `call.principal` function. `OAuthAccessTokenResponse` allows you to access a token and other parameters
returned by the OAuth server.

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="67-87,114"}

In this example, the following actions are performed after receiving a token:

* The token is saved in a [Session](server-sessions.md), which content can be accessed inside other routes.
* The user is redirected to the next route where a request to Google API is made.
* If the requested route is not found, the user is redirected to the `/home` route.

### Step 5: Make a request to API {id="request-api"}

After receiving a token inside the [redirect route](#redirect-route) and saving it to a session, you can make the
request to external APIs using this token. The code snippet below shows how to use the [HttpClient](#create-http-client)
to make such a request and get a user's information by sending this token in the `Authorization` header.

Create a new function called `getPersonalGreeting` which will make the request and return the response body:

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="117-124"}

Then, you can call the function within a `get` route to retrieve a user's information:

```kotlin
```

{src="snippets/auth-oauth-google/src/main/kotlin/com/example/oauth/google/Application.kt" include-lines="104-110"}

For the complete runnable example,
see [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-oauth-google). 
