[//]: # (title: JSON Web Tokens)

<include src="lib.xml" include-id="outdated_warning"/>

<microformat>
<p>Code examples:</p>
<p><a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-jwt">auth-jwt</a></p>
<p><a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-jwk">auth-jwk</a></p>
</microformat>

Ktor supports [JWT (JSON Web Tokens)](https://jwt.io/), which is a mechanism for authenticating JSON-encoded payloads.
It is useful to create stateless authenticated APIs in the standard way, since there are client libraries for it
in a myriad of languages.

This plugin (previously known as feature) will handle `Authorization: Bearer <JWT-TOKEN>`.



Ktor has a couple of classes to use the JWT Payload as `Credential` or as `Principal`.

```kotlin
class JWTCredential(val payload: Payload) : Credential
class JWTPrincipal(val payload: Payload) : Principal
```


## Add dependencies {id="add_dependencies"}
To enable `JWT` and `JWK` authentication, you need to include the `ktor-auth` and `ktor-auth-jwt` artifacts in the build script:

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
        <![CDATA[
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-auth</artifactId>
            <version>${ktor_version}</version>
        </dependency>
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-auth-jwt</artifactId>
            <version>${ktor_version}</version>
        </dependency>
        ]]>
        </code>
   </tab>
</tabs>



## Configuring server/routes

JWT and JWK each have their own method with slightly different parameters. 
Both require the `realm` parameter, which is used in the WWW-Authenticate response header.

## Using a verifier and a validator

The verifier will use the secret to verify the signature to trust the source.
You can also check the payload within `validate` callback to ensure everything is right and to produce a Principal.

### application.conf:

```kotlin
```
{src="snippets/auth-jwt/src/main/resources/application.conf" lines="11-15"}

### JWT auth

```kotlin
```
{src="snippets/auth-jwt/src/main/kotlin/com/example/Application.kt" lines="12-32"}


## Using a JWK provider

```kotlin
```
{src="snippets/auth-jwk/src/main/kotlin/com/example/Application.kt" lines="14-34"}
