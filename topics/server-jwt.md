[//]: # (title: JSON Web Tokens)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>

<var name="plugin_name" value="Authentication JWT"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-auth</code>, <code>io.ktor:ktor-server-auth-jwt</code>
</p>
<p>
<b>Code examples</b>: 
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-jwt-hs256">auth-jwt-hs256</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-jwt-rs256">auth-jwt-rs256</a>
</p>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

<link-summary>
The %plugin_name% plugin allows you to authenticate clients using Json Web Token. 
</link-summary>

[JSON Web Token (JWT)](https://jwt.io/) is an open standard that defines a way for securely transmitting information between parties as a JSON object. This information can be verified and trusted since it is signed using a shared secret (with the `HS256` algorithm) or a public/private key pair (for example, `RS256`).

Ktor handles JWTs passed in the `Authorization` header using the `Bearer` schema and allows you to:
* verify the signature of a JSON web token;
* perform additional validations on the JWT payload.

> You can get general information about authentication and authorization in Ktor in the [](server-auth.md) section.


## Add dependencies {id="add_dependencies"}
To enable `JWT`authentication, you need to include the `ktor-server-auth` and `ktor-server-auth-jwt` artifacts in the build script:

<tabs group="languages">
    <tab title="Gradle (Kotlin)" group-key="kotlin">
        <code-block lang="Kotlin" title="Sample">
            implementation("io.ktor:ktor-server-auth:$ktor_version")
            implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
        </code-block>
    </tab>
    <tab title="Gradle (Groovy)" group-key="groovy">
        <code-block lang="Groovy" title="Sample">
            implementation "io.ktor:ktor-server-auth:$ktor_version"
            implementation "io.ktor:ktor-server-auth-jwt:$ktor_version"
        </code-block>
    </tab>
    <tab title="Maven" group-key="maven">
        <code-block lang="XML" title="Sample">
&lt;dependency&gt;
&lt;groupId&gt;io.ktor&lt;/groupId&gt;
&lt;artifactId&gt;ktor-server-auth-jvm&lt;/artifactId&gt;
&lt;version&gt;${ktor_version}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
&lt;groupId&gt;io.ktor&lt;/groupId&gt;
&lt;artifactId&gt;ktor-server-auth-jwt-jvm&lt;/artifactId&gt;
&lt;version&gt;${ktor_version}&lt;/version&gt;
&lt;/dependency&gt;
        </code-block>
   </tab>
</tabs>


## JWT authorization flow {id="flow"}
The JWT authorization flow in Ktor might look as follows:
1. A client makes a `POST` request with the credentials to a specific authentication [route](server-routing.md) in a server application. The example below shows an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) `POST` request with the credentials passed in JSON:
   ```HTTP
   ```
   {src="snippets/auth-jwt-hs256/requests.http" include-lines="2-8"}
2. If the credentials are valid, a server generates a JSON web token and signs it with the specified algorithm. For example, this might be `HS256` with a specific shared secret or `RS256` with a public/private key pair.
3. A server sends a generated JWT to a client.
4. A client can now make a request to a protected resource with a JSON web token passed in the `Authorization` header using the `Bearer` schema.
   ```HTTP
   ```
   {src="snippets/auth-jwt-hs256/requests.http" include-lines="13-14"}
5. A server receives a request and performs the following validations:
   * Verifies a token's signature. Note that a [verification way](#configure-verifier) depends on the algorithm used to sign a token.
   * Perform [additional validations](#validate-payload) on the JWT payload.
6. After validation, a server responds with the contents of a protected resource.


## Install JWT {id="install"}
To install the `jwt` authentication provider, call the [jwt](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-jwt/io.ktor.server.auth.jwt/jwt.html) function inside the `install` block:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
//...
install(Authentication) {
    jwt {
        // Configure jwt authentication
    }
}
```
You can optionally specify a [provider name](server-auth.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).


## Configure JWT {id="configure-jwt"}
In this section, we'll see how to use JSON web tokens in a server Ktor application. We'll demonstrate two approaches to signing tokens since they require slightly different ways to verify tokens:
* Using `HS256` with a specified shared secret. 
* Using `RS256` with a public/private key pair.

You can find complete projects here: [auth-jwt-hs256](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-jwt-hs256), [auth-jwt-rs256](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-jwt-rs256).

### Step 1: Configure JWT settings {id="jwt-settings"}

To configure JWT-related settings, you can create a custom `jwt` group in a [configuration file](server-configuration-file.topic). For example, the `application.conf` file might look as follows:

<tabs group="sign-alg">
<tab title="HS256" group-key="hs256">

```
```
{style="block" src="snippets/auth-jwt-hs256/src/main/resources/application-custom.conf" include-lines="11-16"}

</tab>
<tab title="RS256" group-key="rs256">

```
```
{style="block" src="snippets/auth-jwt-rs256/src/main/resources/application.conf" include-lines="11-16"}

</tab>
</tabs>

> Note that secret information should not be stored in the configuration file as plain text. Consider using [environment variables](server-configuration-file.topic#environment-variables) to specify such parameters.
>
{type="warning"}

You can [access these settings in code](server-configuration-file.topic#read-configuration-in-code) in the following way:

<tabs group="sign-alg">
<tab title="HS256" group-key="hs256">

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="24-27"}

</tab>
<tab title="RS256" group-key="rs256">

```kotlin
```
{style="block" src="snippets/auth-jwt-rs256/src/main/kotlin/com/example/Application.kt" include-lines="31-34"}

</tab>
</tabs>



### Step 2: Generate a token {id="generate"}

To generate a JSON web token, you can use [JWTCreator.Builder](https://javadoc.io/doc/com.auth0/java-jwt/latest/com/auth0/jwt/JWTCreator.Builder.html). Code snippets below show how to do this for both `HS256` and `RS256` algorithms:

<tabs group="sign-alg">
<tab title="HS256" group-key="hs256">

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="50-61"}

</tab>
<tab title="RS256" group-key="rs256">

```kotlin
```
{style="block" src="snippets/auth-jwt-rs256/src/main/kotlin/com/example/Application.kt" include-lines="58-72"}

</tab>
</tabs>

1. `post("/login")` defines an authentication [route](server-routing.md) for receiving `POST` requests.
2. `call.receive<User>()` [receives](server-serialization.md#receive_data) user credentials sent as a JSON object and converts it to a `User` class object.
3. `JWT.create()` generates a token with the specified JWT settings, adds a custom claim with a received username, and signs a token with the specified algorithm:
   * For `HS256`, a shared secret is used to sign a token.
   * For `RS256`, a public/private key pair is used.
4. `call.respond` [sends](server-serialization.md#send_data) a token to a client as a JSON object.



### Step 3: Configure realm {id="realm"}
The `realm` property allows you to set the realm to be passed in the `WWW-Authenticate` header when accessing a [protected route](#authenticate-route).

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="27-30,46-47"}


### Step 4: Configure a token verifier {id="configure-verifier"}

The `verifier` function allows you to verify a token format and its signature:
* For `HS256`, you need to pass a [JWTVerifier](https://www.javadoc.io/doc/com.auth0/java-jwt/latest/com/auth0/jwt/JWTVerifier.html) instance to verify a token.
* For `RS256`, you need to pass [JwkProvider](https://www.javadoc.io/doc/com.auth0/jwks-rsa/latest/com/auth0/jwk/JwkProvider.html), which specifies a JWKS endpoint for accessing a public key used to verify a token. In our case, an issuer is `http://0.0.0.0:8080`, so a JWKS endpoint address will be `http://0.0.0.0:8080/.well-known/jwks.json`.


<tabs group="sign-alg">
<tab title="HS256" group-key="hs256">

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="24-35,46-47"}

</tab>
<tab title="RS256" group-key="rs256">

```kotlin
```
{style="block" src="snippets/auth-jwt-rs256/src/main/kotlin/com/example/Application.kt" include-lines="32-44,55-56"}

</tab>
</tabs>


### Step 5: Validate JWT payload {id="validate-payload"}

1. The `validate` function allows you to perform additional validations on the JWT payload. Check the `credential` parameter, which represents a [JWTCredential](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-jwt/io.ktor.server.auth.jwt/-j-w-t-credential/index.html) object and contains the JWT payload. In the example below, the value of a custom `username` claim is checked.
   ```kotlin
   ```
   {style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="28-29,36-42,46-47"}
   
   In the case of successful authentication, return [JWTPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-jwt/io.ktor.server.auth.jwt/-j-w-t-principal/index.html). 
2. The `challenge` function allows you to configure a response to be sent if authentication fails.
   ```kotlin
   ```
   {style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="28-29,43-47"}







### Step 6: Protect specific resources {id="authenticate-route"}

After configuring the `jwt` provider, you can protect specific resources in our application using the **[authenticate](server-auth.md#authenticate-route)** function. In the case of successful authentication, you can retrieve an authenticated [JWTPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth-jwt/io.ktor.server.auth.jwt/-j-w-t-principal/index.html) inside a route handler using the `call.principal` function and get the JWT payload. In the example below, the value of a custom `username` claim and a token expiration time are retrieved.

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" include-lines="49,63-71"}
