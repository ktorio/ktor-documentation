[//]: # (title: Basic authentication)

<microformat>
<var name="example_name" value="client-auth-basic"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The Basic [authentication scheme](auth.md) can be used for logging in users. In this scheme, user credentials are transmitted as username/password pairs encoded using Base64. 


## Basic authentication flow {id="flow"}

The basic authentication flow looks as follows:

1. A client makes a request without the `Authorization` header to a specific resource in a server application.
2. A server responds to a client with a `401` (Unauthorized) response status and uses a `WWW-Authenticate` response header to provide information that the basic authentication scheme is used to protect a route. A typical `WWW-Authenticate` header looks like this:

   ```
   WWW-Authenticate: Basic realm="Access to the '/' path", charset="UTF-8"
   ```
   {style="block"}

   The Ktor client allows you to send credentials without waiting the `WWW-Authenticate` header using the `sendWithoutRequest` [function](#configure).

4. Usually a client displays a login dialog where a user can enter credentials. Then, a client makes a request with the `Authorization` header containing a username and password pair encoded using Base64, for example:

   ```
   Authorization: Basic amV0YnJhaW5zOmZvb2Jhcg
   ```
   {style="block"}

5. A server validates credentials sent by the client and responds with the requested content.


## Configure basic authentication {id="configure"}

To send user credentials in the `Authorization` header using the `Basic` scheme, you need to configure the `basic` authentication provider as follows:

1. Call the [basic](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/basic.html) function inside the `install` block.
2. Provide the required credentials using [BasicAuthCredentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-basic-auth-credentials/index.html) and pass this object to the [credentials](https://api.ktor.io/ktor-client/ktor-client-features/ktor-client-auth/ktor-client-auth/io.ktor.client.features.auth.providers/-basic-auth-config/credentials.html) function.
3. Configure the realm using the `realm` property.

   ```kotlin
   ```
   {src="snippets/client-auth-basic/src/main/kotlin/com/example/Application.kt" lines="13-22"}

4. Optionally, enable sending credentials in the initial request without waiting for a `401` (Unauthorized) response with the `WWW-Authenticate` header. You need to call the `sendWithoutRequest` function returning boolean and check the request parameters.

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

> You can find the full example here: [client-auth-basic](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-auth-basic).


