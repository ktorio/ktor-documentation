[//]: # (title: Basic authentication in Ktor Client)

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-auth</code>
</p>
<var name="example_name" value="client-auth-basic"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

The Basic [authentication scheme](client-auth.md) can be used for logging in users. In this scheme, user credentials are transmitted as username/password pairs encoded using Base64. 

> On the server, Ktor provides the [Authentication](server-basic-auth.md) plugin for handling basic authentication.

## Basic authentication flow {id="flow"}

The basic authentication flow looks as follows:

1. A client makes a request to a protected resource in a server application without the `Authorization` header.
2. The server responds with a `401 Unauthorized` response status and uses a `WWW-Authenticate` response header to
   indicate that Basic authentication is required. A typical `WWW-Authenticate` header looks like this:

   ```
   WWW-Authenticate: Basic realm="Access to the '/' path", charset="UTF-8"
   ```
   {style="block"}

   The Ktor client allows you to send credentials preemptively – without waiting for the `WWW-Authenticate` header –
   by using the [`sendWithoutRequest()` function](#configure).

3. The client typically prompts the user for credentials. It then makes a request with the `Authorization` header 
   containing a username and password pair encoded using Base64, for example:

   ```
   Authorization: Basic amV0YnJhaW5zOmZvb2Jhcg
   ```
   {style="block"}

4. The server validates the credentials sent by the client and responds with the requested content.


## Configure basic authentication {id="configure"}

To send user credentials in the `Authorization` header using the `Basic` scheme, you need to configure the `basic`
authentication provider:

1. Call the [`basic`](https://api.ktor.io/ktor-client-auth/io.ktor.client.plugins.auth.providers/basic.html) function 
   inside the `install(Auth)` block.
2. Provide the required credentials using [`BasicAuthCredentials`](https://api.ktor.io/ktor-client-auth/io.ktor.client.plugins.auth.providers/-basic-auth-credentials/index.html) and pass this object to the [`credentials`](https://api.ktor.io/ktor-client-auth/io.ktor.client.plugins.auth.providers/-basic-auth-config/credentials.html) function.
3. Configure the realm using the `realm` property.

   ```kotlin
   ```
   {src="snippets/client-auth-basic/src/main/kotlin/com/example/Application.kt" include-lines="17-26"}

4. (Optional) Enable preemptive authentication using the `sendWithoutRequest` function, which checks the request
   parameters and decides whether to attach credentials to the initial request.

   ```kotlin
   install(Auth) {
       basic {
           // ...
           sendWithoutRequest { request ->
               request.url.host == "0.0.0.0"
           }
       }
   }
   ```
5. (Optional) Disable credential caching. By default, credentials returned by the `credentials {}` provider are cached
   for reuse across requests. You can disable caching with the `cacheTokens` option:

    ```kotlin
    basic {
        cacheTokens = false   // Reloads credentials for every request
        // ...
    }
    ```
    Disabling caching is useful when credentials may change during the client session or must reflect the latest 
    stored state.

    > For details on clearing cached credentials programmatically, see the general [Token caching and cache control](client-auth.md#token-caching)
    > section.

> For a full example of basic authentication in Ktor Client, see [client-auth-basic](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-auth-basic).


