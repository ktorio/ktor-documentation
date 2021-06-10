[//]: # (title: Form-based authentication)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-auth</code>
</p>
<p>
Code examples: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-form">auth-form</a>, <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-form-html-dsl">auth-form-html-dsl</a>
</p>
</microformat>

Form-based authentication uses a [web form](https://developer.mozilla.org/en-US/docs/Learn/Forms) to collect credential information and authenticate a user.

> Given that username and password are passed as clear text when using form-based authentication, you need to use [HTTPS/TLS](ssl.md) to protect sensitive information.


## Add dependencies {id="add_dependencies"}
To enable `form` authentication, you need to include the `ktor-auth` artifact in the build script:
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Form-based authentication flow {id="flow"}

The form-based authentication flow might look as follows:

1. An unauthenticated client makes a request to a specific [route](Routing_in_Ktor.md) in a server application.
1. A server returns an HTML page that consists at least from an HTML-based web form, which prompts a user for a username and password. 
   > Ktor allows you to build a form using [Kotlin DSL](html_dsl.md), or you can choose between various JVM template engines, such as Freemarker, Velocity, and so on.
1. When a user submits a username and password, a client makes a request containing web form data (which includes the username and password) to a server.
   
   ```kotlin
   ```
   {src="snippets/auth-form/post.http"}
   
   In Ktor, you need to [specify parameter names](#configure-provider) used to fetch a username and password.

1. A server [validates](#configure-provider) credentials sent by a client and responds with the requested content.


## Install form authentication {id="install"}
To install the `form` authentication provider, call [form](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/form.html) function inside the `install` block:

```kotlin
install(Authentication) {
    form {
        // Configure form authentication
    }
}
```

You can optionally specify a [provider name](authentication.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure form authentication {id="configure"}

### Step 1: Configure a form provider {id="configure-provider"}
The `form` authentication provider exposes its settings via the [FormAuthenticationProvider/Configuration](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-form-authentication-provider/-configuration/index.html) class. In the example below, the following settings are specified:
* The `userParamName` and `passwordParamName` properties specify parameter names used to fetch a username and password.
* The `validate` function validates a username and password.

```kotlin
```
{src="snippets/auth-form/src/main/kotlin/com/example/Application.kt" lines="9-20"}

The `validate` function checks `UserPasswordCredential` and returns a `UserIdPrincipal` in a case of successful authentication or `null` if authentication fails.

> As for the `basic` authentication, you can also use [UserHashedTableAuth](basic.md#validate-user-hash) to validate users stored in an in-memory table that keeps usernames and password hashes.

### Step 2: Protect specific routes {id="authenticate-route"}

After configuring the `form` provider, you can protect specific routes using the `authenticate` function. In a case of successful authentication, you can retrieve an authenticated [UserIdPrincipal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-user-id-principal/index.html) inside a route handler using the [call.principal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/principal.html) function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-form/src/main/kotlin/com/example/Application.kt" lines="22-28"}
