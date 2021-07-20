[//]: # (title: JSON Web Tokens)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-auth</code>, <code>io.ktor:ktor-auth-jwt</code>
</p>
<p>
Code examples: 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-jwt-hs256">auth-jwt-hs256</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-jwt-rs256">auth-jwt-rs256</a>
</p>
</microformat>

[JSON Web Token](https://jwt.io/) is an open standard that defines a way for securely transmitting information between parties as a JSON object. This information can be verified and trusted since it is signed using a shared secret (with the `HS256` algorithm) or a public/private key pair (for example, `RS256`).

Ktor handles JWTs passed in the `Authorization` header using the `Bearer` schema and allows you to:
* verify the signature of a JSON web token;
* perform additional validations on the JWT payload.


## Add dependencies {id="add_dependencies"}
To enable `JWT`authentication, you need to include the `ktor-auth` and `ktor-auth-jwt` artifacts in the build script:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample">
            implementation "io.ktor:ktor-auth:$ktor_version"
            implementation "io.ktor:ktor-auth-jwt:$ktor_version"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("io.ktor:ktor-auth:$ktor_version")
            implementation("io.ktor:ktor-auth-jwt:$ktor_version")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample">
&lt;dependency&gt;
&lt;groupId&gt;io.ktor&lt;/groupId&gt;
&lt;artifactId&gt;ktor-auth&lt;/artifactId&gt;
&lt;version&gt;${ktor_version}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
&lt;groupId&gt;io.ktor&lt;/groupId&gt;
&lt;artifactId&gt;ktor-auth-jwt&lt;/artifactId&gt;
&lt;version&gt;${ktor_version}&lt;/version&gt;
&lt;/dependency&gt;
        </code>
   </tab>
</tabs>


## JWT authorization flow {id="flow"}
The JWT authorization flow in Ktor might look as follows:
1. A client makes a `POST` request with the credentials to a specific authentication [route](Routing_in_Ktor.md) in a server application. The example below shows an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) `POST` request with the credentials passed in JSON:
   ```HTTP
   ```
   {src="snippets/auth-jwt-hs256/requests.http" lines="2-8"}
2. If the credentials are valid, a server generates a JSON web token and signs it with the specified algorithm. For example, this might be `HS256` with a specific shared secret or `RS256` with a public/private key pair.
3. A server sends a generated JWT to a client.
4. A client can now make a request to a protected resource with a JSON web token passed in the `Authorization` header using the `Bearer` schema.
   ```HTTP
   ```
   {src="snippets/auth-jwt-hs256/requests.http" lines="13-14"}
5. A server receives a request and performs the following validations:
   * Verifies a token's signature. Note that a [verification way](#configure-verifier) depends on the algorithm used to sign a token.
   * Perform [additional validations](#validate-payload) on the JWT payload.
6. After validation, a server responds with the contents of a protected resource.


## Install JWT {id="install"}
To install the `jwt` authentication provider, call [jwt](https://api.ktor.io/ktor-features/ktor-auth-jwt/ktor-auth-jwt/io.ktor.auth.jwt/jwt.html?query=fun%20Authentication.Configuration.jwt) function inside the `install` block:

```kotlin
install(Authentication) {
    jwt {
        // Configure jwt authentication
    }
}
```
You can optionally specify a [provider name](authentication.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).


## Configure JWT {id="configure-jwt"}
In this section, we'll see how to use JSON web tokens in a server Ktor application. We'll demonstrate two approaches to signing tokens since they require slightly different ways to verify tokens:
* Using `HS256` with a specified shared secret. 
* Using `RS256` with a public/private key pair.

You can find full runnable projects here: [auth-jwt-hs256](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-jwt-hs256), [auth-jwt-rs256](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-jwt-rs256).

### Step 1: Configure JWT settings {id="jwt-settings"}

To configure JWT-related settings, you can create a custom `jwt` group in the [application.conf](Configurations.xml#hocon-file) configuration file. This file might look as follows:

<tabs>
<tab title="HS256">

```
```
{style="block" src="snippets/auth-jwt-hs256/src/main/resources/application.conf" lines="11-16"}

</tab>
<tab title="RS256">

```
```
{style="block" src="snippets/auth-jwt-rs256/src/main/resources/application.conf" lines="11-16"}

</tab>
</tabs>

> Note that secret information should not be stored under version control.
>
{type="note"}

You can [access these settings in code](Configurations.xml#read-configuration-in-code) in the following way:

<tabs>
<tab title="HS256">

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" lines="23-26"}

</tab>
<tab title="RS256">

```kotlin
```
{style="block" src="snippets/auth-jwt-rs256/src/main/kotlin/com/example/Application.kt" lines="30-33"}

</tab>
</tabs>



### Step 2: Generate a token {id="generate"}

To generate a JSON web token, you can use [JWTCreator.Builder](https://javadoc.io/doc/com.auth0/java-jwt/latest/com/auth0/jwt/JWTCreator.Builder.html). Code snippets below show how to do this for both `HS256` and `RS256` algorithms:

<tabs>
<tab title="HS256">

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" lines="46-57"}

</tab>
<tab title="RS256">

```kotlin
```
{style="block" src="snippets/auth-jwt-rs256/src/main/kotlin/com/example/Application.kt" lines="54-68"}

</tab>
</tabs>

1. `post("/login")` defines an authentication [route](Routing_in_Ktor.md) for receiving `POST` requests.
2. `call.receive<User>()` [receives](serialization.md#receive_data) user credentials sent as a JSON object and converts it to a `User` class object.
3. `JWT.create()` generates a token with the specified JWT settings, adds a custom claim with a received username, and signs a token with the specified algorithm:
   * For `HS256`, a shared secret is used to sign a token.
   * For `RS256`, a public/private key pair is used.
4. `call.respond` [sends](serialization.md#send_data) a token to a client as a JSON object.



### Step 3: Configure realm {id="realm"}
The `realm` property allows you to set the realm to be passed in `WWW-Authenticate` header when accessing a [protected route](#authenticate-route).

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" lines="26-29,42-43"}


### Step 4: Configure a token verifier {id="configure-verifier"}

The [verifier](https://api.ktor.io/ktor-features/ktor-auth-jwt/ktor-auth-jwt/io.ktor.auth.jwt/-j-w-t-authentication-provider/-configuration/verifier.html) function allows you to verify a token format and its signature:
* For `HS256`, you need to pass a [JWTVerifier](https://www.javadoc.io/doc/com.auth0/java-jwt/latest/com/auth0/jwt/JWTVerifier.html) instance to verify a token.
* For `RS256`, you need to pass [JwkProvider](https://www.javadoc.io/doc/com.auth0/jwks-rsa/latest/com/auth0/jwk/JwkProvider.html), which specifies a JWKS endpoint for accessing a public key used to verify a token. In our case, an issuer is `http://0.0.0.0:8080`, so a JWKS endpoint address will be `http://0.0.0.0:8080/.well-known/jwks.json`.


<tabs>
<tab title="HS256">

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" lines="23-34,42-43"}

</tab>
<tab title="RS256">

```kotlin
```
{style="block" src="snippets/auth-jwt-rs256/src/main/kotlin/com/example/Application.kt" lines="31-43,51-52"}

</tab>
</tabs>


### Step 5: Validate JWT payload {id="validate-payload"}

The [validate](https://api.ktor.io/ktor-features/ktor-auth-jwt/ktor-auth-jwt/io.ktor.auth.jwt/-j-w-t-authentication-provider/-configuration/validate.html) function allows you to perform additional validations on the JWT payload in the following way:
1. Check the `credential` parameter, which represents a [JWTCredential](https://api.ktor.io/ktor-features/ktor-auth-jwt/ktor-auth-jwt/io.ktor.auth.jwt/-j-w-t-credential/index.html) object and contains the JWT payload. In the example below, the value of a custom `username` claim is checked.
2. In a case of successful authentication, return [JWTPrincipal](https://api.ktor.io/ktor-features/ktor-auth-jwt/ktor-auth-jwt/io.ktor.auth.jwt/-j-w-t-principal/index.html). If authentication fails, return `null.`

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" lines="27-28,35-43"}




### Step 6: Define authorization scope {id="authenticate-route"}

After configuring the `jwt` provider, you can define the authorization for the different resources in our application using the `authenticate` function. In a case of successful authentication, you can retrieve an authenticated [JWTPrincipal](https://api.ktor.io/ktor-features/ktor-auth-jwt/ktor-auth-jwt/io.ktor.auth.jwt/-j-w-t-principal/index.html) inside a route handler using the [call.principal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/principal.html) function and get the JWT payload. In the example below, the value of a custom `username` claim and a token expiration time are retrieved.

```kotlin
```
{style="block" src="snippets/auth-jwt-hs256/src/main/kotlin/com/example/Application.kt" lines="45,59-67"}
