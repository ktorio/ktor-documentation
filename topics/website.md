[//]: # (title: Website)

<include src="lib.xml" include-id="outdated_warning"/>

In this guide you will learn how to create an HTML Website using Ktor.
We are going to create a simple website with HTML rendered at the back-end with users, a login form,
and keeping a persistent session.

To achieve this, we are going to use the [Routing], [StatusPages], [Authentication], [Sessions], [StaticContent],
[FreeMarker], and [HTML DSL] features.

[Routing]: Routing_in_Ktor.md
[StatusPages]: status_pages.md
[Authentication]: authentication.md
[Sessions]: sessions.md
[StaticContent]: Serving_Static_Content.md
[FreeMarker]: freemarker.md
[HTML DSL]: html_dsl.md





## Setting up the project

The first step is to set up a project. You can follow the [Quick Start](Welcome.md) guide, or use the following form to create one:



## Simple routing

First, we are going to use the routing feature. This feature is part of the Ktor's core, so you won't need
to include any additional artifacts.

This feature is installed automatically when using the `routing { }` block.

Let's start creating a simple GET route that responds with 'OK':

```kotlin
fun Application.module() {
    routing {
        get("/") {
            call.respondText("OK")
        }
    }
}
```

## Serving HTML with FreeMarker

Apache FreeMarker is a template engine for the JVM, and thus you can use it with Kotlin.
There is a Ktor feature supporting it.

For now, we are going to store the templates embedded as part of the resources in a `templates` folder.

Create a file called `resources/templates/index.ftl` and put in the following content to create a simple HTML list:

```html
<#-- @ftlvariable name="data" type="com.example.IndexData" -->
<html>
	<body>
		<ul>
		<#list data.items as item>
			<li>${item}</li>
		</#list>
		</ul>
	</body>
</html>
```

IntelliJ IDEA Ultimate has FreeMarker support with autocompletion and variable hinting.
{.note}

Now, let's install the FreeMarker feature and then create a route serving this template and passing a set of values to it:

```kotlin
data class IndexData(val items: List<Int>)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    
    routing {
        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }
    }
}
```

Now you can run the server and open a browser pointing to <http://127.0.0.1:8080/html-freemarker>{target="_blank"} to see the results:

![](website1.png){.rounded-shadow}

Nice!

## Serving static files: styles, scripts, images... 

In addition to templates, you will want to serve static content.
Static content will serve faster, and is compatible with other features like Partial Content that allows
you to resume downloads or partially download files.

For now, we are going to serve a simple `styles.css` file to apply styles to our simple page.

Serving static files doesn't require installing any features, but it is a plain Route handler.
To serve static files at the `/static` url, from `/resources/static`, you would write the following code:

```kotlin
routing {
    // ...
    static("/static") {
        resources("static")
    }
}
```

Now let's create the `resources/static/styles.css` file with the following content:

```css
body {
    background: #B9D8FF;
}
```

In addition to this, we will have to update our template to include the `style.css` file:
```html
<#-- @ftlvariable name="data" type="com.example.IndexData" -->
<html>
    <head>
        <link rel="stylesheet" href="/static/styles.css">
    </head>
    <body>
	<!-- ... -->
    </body>
</html>
```

And the result:

![](website2.png){.rounded-shadow}

Now we have a colorful website from 1990!

>Static files are not only text files! Try to add an image (what about a fancy animated blinking gif file? 👩🏻‍🎨) to the `static` folder, and include a `<img src="...">` tag to the HTML template.
>
{type="note"}

## Enabling partial content: large files and videos

Though not really needed for this specific case, if you enable partial content support, people will be able
to resume larger static files on connections with frequent problems, or allow seeking support when
serving and watching videos.

Enabling partial content is straightforward:

```kotlin
install(PartialContent) {
}
```

## Creating a form

Now we are going to create a fake login form. To make it simple, we are going to accept users with the same password,
and we are not going to implement a registration form.

Create a `resources/templates/login.ftl`:

```html

<html>
<head>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<#if error??>
    <p style="color:red;">${error}</p>
</#if>
<form action="/login" method="post" enctype="application/x-www-form-urlencoded">
    <div>User:</div>
    <div><input type="text" name="username" /></div>
    <div>Password:</div>
    <div><input type="password" name="password" /></div>
    <div><input type="submit" value="Login" /></div>
</form>
</body>
</html>
```

In addition to the template, we need to add some logic to it. In this case we are going to handle GET and POST methods in different blocks of code:

```kotlin
route("/login") {
    get {
        call.respond(FreeMarkerContent("login.ftl", null))
    }
    post {
        val post = call.receiveParameters()
        if (post["username"] != null && post["username"] == post["password"]) {
            call.respondText("OK")
        } else {
            call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
        }
    }
}
```

As we said, we are accepting `username` with the same `password`, but we are not accepting null values.
If the login is valid, we respond with a single OK for now, while we reuse the template if the login fails
to display the same form but with an error.

## Redirections

In some cases, like route refactoring or forms, we will want to perform redirections (either temporary or permanent).
In this case, we want to temporarily redirect to the homepage upon successful login, instead of replying with plain text.

<compare>

```kotlin
call.respondText("OK")
```

```kotlin
call.respondRedirect("/", permanent = false)
```
</compare>

## Using the Form authentication

To illustrate how to receive POST parameters we have handled the login manually, but we can also use the authentication
feature with a form provider:

```kotlin
install(Authentication) {
    form("login") {
        userParamName = "username"
        passwordParamName = "password"
        challenge = FormAuthChallenge.Unauthorized
        validate { credentials -> if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null }
    }
}
route("/login") {
    get {
        // ...
    }
    authenticate("login") {
        post {
            val principal = call.principal<UserIdPrincipal>()
            call.respondRedirect("/", permanent = false)
        }
    }
}
```

## Sessions

To prevent having to authenticate all the pages, we are going to store the user in a session, and that session will
be propagated to all the pages using a session cookie.

```kotlin
data class MySession(val username: String)

fun Application.module() {
    install(Sessions) {
        cookie<MySession>("SESSION")
    }
    routing {
        authenticate("login") {
            post {
                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                call.sessions.set("SESSION", MySession(principal.name))
                call.respondRedirect("/", permanent = false)
            }
        }
    }
} 
```

Inside our pages, we can try to get the session and produce different results:

```kotlin
fun Application.module() {
    // ...
    get("/") {
        val session = call.sessions.get<MySession>()
        if (session != null) {
            call.respondText("User is logged")
        } else {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }
    }
}
```

## Using HTML DSL instead of FreeMarker

You can choose to generate HTML directly from the code instead of using a Template Engine.
For that you can use the HTML DSL. This DSL doesn't require installation, but requires an additional artifact (see [HTML DSL] for details).
This artifact provides an extension to respond with HTML blocks:

```kotlin
get("/") { 
    val data = IndexData(listOf(1, 2, 3))
    call.respondHtml {
        head {
            link(rel = "stylesheet", href = "/static/styles.css")
        }
        body {
            ul {
                for (item in data.items) {
                    li { +"$item" }                
                }
            }
        }
    }
}
```

The main benefits of an HTML DSL is that you have full statically typed access to variables and it is thoroughly integrated
with the code base.

The downside of all this is that you have to recompile to change the HTML, and you can't search complete HTML blocks.
But it is lightning fast, and you can use the [autoreload feature](Auto_reload.xml) to recompile
on change and reload the relevant JVM classes.

## Exercises

### Exercise 1

Make a registration page and store the user/password datasource in memory in a hashmap.

### Exercise 2

Use a database to store the users.