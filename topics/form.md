[//]: # (title: Form authentication)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-auth</code>
</p>
<p>
Code examples: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-form">auth-form</a>, <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-form-html-dsl">auth-form-html-dsl</a>
</p>
</microformat>

Form or form-based authentication uses a [web form](https://developer.mozilla.org/en-US/docs/Learn/Forms) to collect credential information and authenticate a user.

> Given that user name and password are passed as clear text when using form-based authentication, you need to use [HTTPS/TLS](ssl.md) to protect sensitive information.


## Add dependencies {id="add_dependencies"}
To enable `form` authentication, you need to include the `ktor-auth` artifact in the build script:
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Form-based authentication flow {id="flow"}

The form-based authentication flow might look as follows:

1. An unauthenticated client makes a request to a specific [route](Routing_in_Ktor.md) in a server application.
1. A server returns an HTML page that consists at least from an HTML-based web form, which prompts a user for a user name and password. 
   > Ktor allows you to build a form using [Kotlin DSL](html_dsl.md), or you can choose between various JVM template engines, such as Freemarker, Velocity, and so on.
1. When a user submits a name and password, the client makes a request with web form data (which includes the username and password) to a server.
   
   ```kotlin
   ```
   {src="snippets/auth-form/post.http"}
   
   In Ktor, you need to [specify parameter names](#configure-provider) used to fetch a user name and password.

1. A server [validates](#configure-provider) credentials sent by a client and responds with the requested content.


## Install form authentication {id="install"}
To install the `form` authentication provider, call [form](https://api.ktor.io/%ktor_version%/io.ktor.auth/form.html) function inside the `install` block:

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

### Step 2: Protect specific routes {id="authenticate-route"}


## Usage {id="usage"} 

The `form` authentication uses a username and password as credentials:

```kotlin
```
{src="snippets/auth-form/src/main/kotlin/com/example/Application.kt" lines="9-20"}

The `validate` method provides a callback that must generate a Principal from given a `UserPasswordCredential`
or null for invalid credentials. That callback is marked as *suspending*, so that you can validate credentials in an asynchronous fashion.