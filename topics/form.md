[//]: # (title: Form-based authentication)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<p>
<b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-form-html-dsl">auth-form-html-dsl</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-form-session">auth-form-session</a>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

Form-based authentication uses a [web form](https://developer.mozilla.org/en-US/docs/Learn/Forms) to collect credential information and authenticate a user.
To create a web form in Ktor, you can use [HTML DSL](html_dsl.md#html_response) or choose between JVM [template engines](Templating.md), such as FreeMarker, Velocity, and so on.

> Given that username and password are passed as a clear text when using form-based authentication, you need to use [HTTPS/TLS](ssl.md) to protect sensitive information.


## Add dependencies {id="add_dependencies"}
To enable `form` authentication, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Form-based authentication flow {id="flow"}

The form-based authentication flow might look as follows:

1. An unauthenticated client makes a request to a specific [route](Routing_in_Ktor.md) in a server application.
2. A server returns an HTML page that consists at least from an HTML-based web form, which prompts a user for a username and password. 
   > Ktor allows you to build a form using [Kotlin DSL](html_dsl.md), or you can choose between various JVM template engines, such as FreeMarker, Velocity, and so on.
3. When a user submits a username and password, a client makes a request containing web form data (which includes the username and password) to a server.
   
   ```kotlin
   ```
   {src="snippets/auth-form-html-dsl/post.http"}
   
   In Ktor, you need to [specify parameter names](#configure-provider) used to fetch a username and password.

4. A server [validates](#configure-provider) credentials sent by a client and responds with the requested content.


## Install form authentication {id="install"}
To install the `form` authentication provider, call the [form](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/form.html) function inside the `install` block:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
// ...
install(Authentication) {
    form {
        // Configure form authentication
    }
}
```

You can optionally specify a [provider name](authentication.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure form authentication {id="configure"}

### Step 1: Configure a form provider {id="configure-provider"}
The `form` authentication provider exposes its settings via the [FormAuthenticationProvider.Config](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-form-authentication-provider/-config/index.html) class. In the example below, the following settings are specified:
* The `userParamName` and `passwordParamName` properties specify parameter names used to fetch a username and password.
* The `validate` function validates a username and password.

```kotlin
```
{src="snippets/auth-form-html-dsl/src/main/kotlin/com/example/Application.kt" include-lines="11-23"}

The `validate` function checks `UserPasswordCredential` and returns a `UserIdPrincipal` in the case of successful authentication or `null` if authentication fails.

> As for the `basic` authentication, you can also use [UserHashedTableAuth](basic.md#validate-user-hash) to validate users stored in an in-memory table that keeps usernames and password hashes.

### Step 2: Define authorization scope {id="authenticate-route"}

After configuring the `form` provider, you can define the authorization for the different resources in our application using the **[authenticate](authentication.md#authenticate-route)** function. In the case of successful authentication, you can retrieve an authenticated [UserIdPrincipal](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auth/io.ktor.server.auth/-user-id-principal/index.html) inside a route handler using the `call.principal` function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-form-html-dsl/src/main/kotlin/com/example/Application.kt" include-lines="25-30,51"}
