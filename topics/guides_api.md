[//]: # (title: HTTP API)

<include src="lib.md" include-id="outdated_warning"/>



In this guide you will learn how to create an API using ktor.
We are going to create a simple API to store simple text snippets (like a small pastebin-like API).

To achieve this, we are going to use the [Routing], [StatusPages], [Authentication], [JWT Authentication],
[CORS], [ContentNegotiation] and [Jackson] features.

[Routing]: Routing_in_Ktor.md
[StatusPages]: status_pages.md
[Authentication]: features_authentication.md
[JWT Authentication]: jwt.md
[CORS]: cors.md
[ContentNegotiation]: serialization.md
[Jackson]: jackson.md

>While many frameworks advocate how to create REST API's the majority aren't actually talking about REST APIs but HTTP APIs.
>Ktor, much like many other frameworks can be used to create systems that comply with REST constraints. However,
>this tutorial is not talking about REST but HTTP APIs, i.e. endpoints using HTTP verbs that may or may not return JSON, XML or any other format.
>If you want to learn more about RESTful systems, you can start reading [](https://en.wikipedia.org/wiki/Representational_state_transfer){target="_blank"}.
>
{type="note"}


## Setting up the project

The first step is to set up a project. You can follow the [Quick Start](Welcome.md) guide, or use the following form to create one:



## Simple routing

First, we are going to use the [routing feature](Routing_in_Ktor.md). This feature is part of the Ktor's core, so you won't need
to include any additional artifacts.

This feature is installed automatically when using the `routing { }` DSL block.

Let's start creating a simple GET route that responds with `OK` by using the `get` method available inside the `routing` block:

```kotlin
fun Application.module() {
    routing {
        get("/snippets") {
            call.respondText("OK")
        }
    }
}
```

## Serving JSON content

A HTTP API usually responds with JSON. You can use the [Content Negotiation](serialization.md) feature with [Jackson](jackson.md) for this:

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        jackson {
        }
    }
    routing {
        // ...
    }
}
```

To respond to a request with a JSON, you have to call the `call.respond` method with an arbitrary object.

```kotlin
routing {
    get("/snippets") {
        call.respond(mapOf("OK" to true))
    }
}
```

Now the browser or client should respond to `http://127.0.0.1:8080/snippets` with `{"OK":true}`

>If you get an error like `Response pipeline couldn't transform '...' to the OutgoingContent`, check that you have
>installed the [ContentNegotiation](serialization.md) feature with Jackson.
>
{type="note"}

You can also use typed objects as part of the reply (but ensure that your classes are not defined
inside a function or it won't work). So for example:

```kotlin
data class Snippet(val text: String)

val snippets = Collections.synchronizedList(mutableListOf(
    Snippet("hello"),
    Snippet("world")
))

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }
    routing {
        get("/snippets") {
            call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
        }
    }
}
```

Would reply with:

![](snippets_get.png){.rounded-shadow}

## Handling other HTTP methods

HTTP APIs use most of the HTTP methods/verbs (_HEAD_, _GET_, _POST_, _PUT_, _PATCH_, _DELETE_, _OPTIONS_) to perform operations.
Let's create a route to add new snippets. For this, we will need to read the JSON body of the POST request.
For this we will use `call.receive<Type>()`:

```kotlin
data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

// ...

routing {
    get("/snippets") {
        call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
    }
    post("/snippets") {
        val post = call.receive<PostSnippet>()
        snippets += Snippet(post.snippet.text)
        call.respond(mapOf("OK" to true))
    }
}
```

Now it is time to actually try our backend.

If you have IntelliJ IDEA Ultimate, you can use its built-in powerful HTTP Request client,
if not, you can also use postman or curl:

### IntelliJ IDEA Ultimate:
{id="first-request-intellij"}

IntelliJ IDEA Ultimate, along PhpStorm and other IDEs from JetBrains include a
very nice [Editor-Based Rest Client](https://blog.jetbrains.com/phpstorm/2017/09/editor-based-rest-client/){target="_blank"}.

First you have to create a HTTP Request file (either `api` or `http` extensions)
![](IU-http-new-file.png)

Then you have to type the method, url, headers and payload like this:

![](IU-http-request.png)

```text
POST http://127.0.0.1:8080/snippets
Content-Type: application/json

{"snippet": {"text" : "mysnippet"}}
```

And then in the play gutter icon from the URL, you can perform the call, and get the response:

![](IU-http-response.png)

And that's it!

>This allows you to define files (plain or scratches) that include definition for several HTTP requests,
>allowing to include headers, provide a payload inline, or from files, use environment variables defined in a JSON file,
>process the response using JavaScript to perform assertions, or to store some environment variables like
>authentication credentials so they are available to other requests. It supports autocompletion, templates, and
>automatic language injection based on Content-Type, including JSON, XML, etc..
>
{type="note"}

>In addition to easily test your backends inside your editor, it also helps your to document your APIs
>by including a file with the endpoints on it.
>And allows you fetch and locally store responses and visually compare them.
>
{type="note"}

### CURL:
{id="first-request-curl"}

<compare>

```bash
curl \
  --request POST \
  --header "Content-Type: application/json" \
  --data '{"snippet" : {"text" : "mysnippet"}}' \
  http://127.0.0.1:8080/snippets
```


```json
{
  "OK" : true
}
```

</compare>

Let's do the GET request again:

![](snippets_get_new.png){.rounded-shadow}

Nice!

## Grouping routes together

Now we have two separate routes that share the path (but not the method) and we don't want to repeat ourselves.

We can group routes with the same prefix, using the `route(path) { }` block. For each HTTP method, there is an
overload without the route path argument that we can use at routing leaf nodes:

```kotlin
routing {
    route("/snippets") {
        get {
            call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
        }
        post {
            val post = call.receive<PostSnippet>()
            snippets += Snippet(post.snippet.text)
            call.respond(mapOf("OK" to true))
        }
    }
}
```

## Authentication

It would be a good idea to prevent everyone from posting snippets. For now, we are going to limit it using
http's basic authentication with a fixed user and password. To do it, we are going to use the authentication feature.

```kotlin
fun Application.module() {
    install(Authentication) {
        basic {
            realm = "myrealm" 
            validate { if (it.name == "user" && it.password == "password") UserIdPrincipal("user") else null }
        }
    }
    // ...
}
```

After installing and configuring the feature, we can group some routes together to be authenticated with the
`authenticate { }` block.

In our case, we are going to keep the get call unauthenticated, and going to require authentication for the post one:

```kotlin
routing {
    route("/snippets") {
        get {
            call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
        }
        authenticate {
            post {
                val post = call.receive<PostSnippet>()
                snippets += Snippet(post.snippet.text)
                call.respond(mapOf("OK" to true))
            }        
        }
    }
}
```

## JWT Authentication

Instead of using a fixed authentication, we are going to use JWT tokens.

We are going to add a login-register route. That route will register a user if it doesn't exist,
and for a valid login or register it will return a JWT token.
The JWT token will hold the user name, and posting will link a snippet to the user.

We will need to install and configure JWT (replacing the basic auth):

```kotlin
open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

fun Application.module() {
    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }
    // ...
}
```

We will also need a data source holding usernames and passwords. One simple option would be:

```kotlin
class User(val name: String, val password: String)

val users = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)
class LoginRegister(val user: String, val password: String)

```

With all this, we can already create a route for logging or registering users:

```kotlin
routing {
    post("/login-register") {
        val post = call.receive<LoginRegister>()
        val user = users.getOrPut(post.user) { User(post.user, post.password) }
        if (user.password != post.password) error("Invalid credentials")
        call.respond(mapOf("token" to simpleJwt.sign(user.name)))
    }
}
```

Now we can already try to obtain a JWT token for our user:


### IntelliJ


Using the Editor-Based HTTP client for IntelliJ IDEA Ultimate,
you can make the POST request, and check that the content is valid,
and store the token in an environment variable:

![](IU-http-login-register-request.png)

![](IU-http-login-register-response.png)

Now you can make a request using the environment variable `{% raw %}{{auth_token}}{% endraw %}`:

![](IU-http-snippets-env-auth_token-request.png)

![](IU-http-snippets-env-auth_token-response.png)

If you want to easily test different endpoints in addition to localhost,
you can create a `http-client.env.json` file and put a map with environments
and variables like this:

![](http-client.env.json.png)

After this, you can start using the user-defined `{% raw %}{{host}}{% endraw %}` env variable:
![](use_host_env.png)

When trying to run a request, you will be able to choose the environment to use:
![](select_env_for_running.png)


### Curl

<compare>

```bash
curl -v \
  --request POST \
  --header "Content-Type: application/json" \
  --data '{"user" : "test", "password" : "test"}' \
  http://127.0.0.1:8080/login-register
```


```json
{
  "token" : "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdCJ9.96At6bwFhxebk4xk4tpkOFj-3ThxkLFNHkHaKoedOfA"
}
```

</compare>

And with that token, we can already publish snippets:

<compare>

```bash
curl -v \
  --request POST \
  --header "Content-Type: application/json" \
  --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdCJ9.96At6bwFhxebk4xk4tpkOFj-3ThxkLFNHkHaKoedOfA" \
  --data '{"snippet" : {"text": "hello-world-jwt"}}' \
  http://127.0.0.1:8080/snippets
```

```json
{
  "OK" : true
}
```

</compare>

## Associating users to snippets

Since we are posting snippets with an authenticated route, we have access to the generated `Principal` that includes
the username. So we should be able to access that user and associate it to the snippet.

First of all, we will need to associate user information to snippets:

```kotlin
data class Snippet(val user: String, val text: String)

val snippets = Collections.synchronizedList(mutableListOf(
    Snippet(user = "test", text = "hello"),
    Snippet(user = "test", text = "world")
))
```

Now we can use the principal information (that is generated by the authentication feature when authenticating JWT)
when inserting new snippets:

```kotlin
routing {
    // ...
    route("/snippets") {
        // ...
        authenticate {
            post {
                val post = call.receive<PostSnippet>()
                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                snippets += Snippet(principal.name, post.snippet.text)
                call.respond(mapOf("OK" to true))
            }
        }
    }
}
```

Let's try this:

![](final-request.png)

![](final-response.png)

<compare>

```bash
curl -v \
  --request GET \
  http://127.0.0.1:8080/snippets
```


```json
{
  "snippets" : [ {
    "user" : "test",
    "text" : "hello"
  }, {
    "user" : "test",
    "text" : "world"
  }, {
    "user" : "test",
    "text" : "hello-world-jwt"
  } ]
}
```

</compare>

Awesome!

## StatusPages

Now let's refine things a bit. A HTTP API should use HTTP Status codes to provide semantic information about errors.
Right now, when an exception is thrown (for example when trying to get a JWT token from an user that already exists,
but with a wrong password), a 500 server error is returned. We can do it better, and the StatusPages features
will allow you to do this by capturing specific exceptions and generating the result.

Let's create a new exception type:

```kotlin
class InvalidCredentialsException(message: String) : RuntimeException(message)
```

Now, let's install the StatusPages feature, register this exception type, and generate an Unauthorized page: 

```kotlin
fun Application.module() {
    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }
    // ...
}
```

We should also update our login-register page to throw this exception:

```kotlin
routing {
    post("/login-register") {
        val post = call.receive<LoginRegister>()
        val user = users.getOrPut(post.user) { User(post.user, post.password) }
        if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
        call.respond(mapOf("token" to simpleJwt.sign(user.name)))
    }
}
```

Let's try this:

![](bad-credentials-request.png)

![](bad-credentials-response.png)

Things are getting better!

## CORS

Now suppose we need this API to be accessible via JavaScript from another domain. We will need to configure CORS.
And Ktor has a feature to configure this:

```kotlin
fun Application.module() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }
    // ...
}
```

Now our API is accessible from any host :)

## Full Source


```kotlin
package com.example

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.fasterxml.jackson.databind.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val simpleJwt = SimpleJWT("my-super-secret-for-jwt")
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }
    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }
    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }
    routing {
        post("/login-register") {
            val post = call.receive<LoginRegister>()
            val user = users.getOrPut(post.user) { User(post.user, post.password) }
            if (user.password != post.password) throw InvalidCredentialsException("Invalid credentials")
            call.respond(mapOf("token" to simpleJwt.sign(user.name)))
        }
        route("/snippets") {
            get {
                call.respond(mapOf("snippets" to synchronized(snippets) { snippets.toList() }))
            }
            authenticate {
                post {
                    val post = call.receive<PostSnippet>()
                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                    snippets += Snippet(principal.name, post.snippet.text)
                    call.respond(mapOf("OK" to true))
                }
            }
        }
    }
}

data class PostSnippet(val snippet: PostSnippet.Text) {
    data class Text(val text: String)
}

data class Snippet(val user: String, val text: String)

val snippets = Collections.synchronizedList(mutableListOf(
    Snippet(user = "test", text = "hello"),
    Snippet(user = "test", text = "world")
))

open class SimpleJWT(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm).build()
    fun sign(name: String): String = JWT.create().withClaim("name", name).sign(algorithm)
}

class User(val name: String, val password: String)

val users = Collections.synchronizedMap(
    listOf(User("test", "test"))
        .associateBy { it.name }
        .toMutableMap()
)

class InvalidCredentialsException(message: String) : RuntimeException(message)

class LoginRegister(val user: String, val password: String)
```

<tabs>

```text
# Get all the snippets
GET {{host}}/snippets

###

# Register a new user
POST {{host}}/login-register
Content-Type: application/json

{"user" : "test", "password" : "test"}

> {%
client.assert(typeof response.body.token !== "undefined", "No token returned");
client.global.set("auth_token", response.body.token);
%}

###

# Put a new snippet (requires registering)
POST {{host}}/snippets
Content-Type: application/json
Authorization: Bearer {{auth_token}}

{"snippet" : {"text": "hello-world-jwt"}}

###

# Try a bad login-register
POST http://127.0.0.1:8080/login-register
Content-Type: application/json

{"user" : "test", "password" : "invalid-password"}

###
```



```json
{
  "localhost": {
    "host": "http://127.0.0.1:8080"
  },
  "prod": {
    "host": "https://my.domain.com"
  }
}
```

</tabs>

## Exercises

After following this guide, as an exercise, you can try to do the following exercises:

### Exercise 1

Add unique ids to each snippet and add a DELETE http verb to `/snippets` allowing an authenticated user to delete
her snippets.  

### Exercise 2

Store users and snippets in a database. 