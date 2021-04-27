[//]: # (title: Authentication and authorization)

<microformat>
<var name="example_name" value="auth"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

<include src="lib.md" include-id="outdated_warning"/>


Ktor supports authentication out of the box as a standard pluggable feature.
It supports mechanisms to read *credentials*, and to authenticate *principals*.

It can be used in some cases along with the [sessions feature](sessions.md)
to keep the login information between requests.

## Add dependencies {id="add_dependencies"}
To enable authentication, you need to include the `ktor-auth` artifact in the build script:

<var name="artifact_name" value="ktor-auth"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

Note that some authentication features (for example [JWT/JWK](jwt.md) and [LDAP](ldap.md)) might require additional artifacts.


## Basic usage

Ktor defines two concepts: credentials and principals.

* A principal is something that can be authenticated: a user, a computer, a group, etc.
* A credential is an object that represents a set of properties for the server to authenticate a principal:
  a user/password, an API key or an authenticated payload signature, etc.

To install it, you have to call to `application.install(Authentication)`. You have to install this feature
directly to the application and it *won't* work in another `ApplicationCallPipeline` like `Route`.

>You might still be able to call the install code inside a Route if you have the Application injected in a nested DSL,
>but it will be applied to the application itself.
>
{type="note"}

Using its DSL, it allows you to configure the authentication providers available:

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="9-18"}


After defining one or more authentication providers (named or unnamed), with the [routing feature](Routing_in_Ktor.md)
you can create a route group, that will apply that authentication to all the routes defined in that group:

```kotlin
routing {
    authenticate("myauth1") {
        get("/authenticated/route1") {
            // ...
        }    
        get("/other/route2") {
            // ...
        }    
    }
    get("/") {
        // ...
    }
}
```

You can specify several names to apply several authentication providers, or none or null to use the unnamed one.

You can get the generated `Principal` instance inside your handler with:

```kotlin
val principal: UserIdPrincipal? = call.authentication.principal<UserIdPrincipal>()
```

>In the generic, you have to put a specific type that *must* match the generated Principal.
>It will return null in the case you provide another type.
>
{type="note"}

>The handler won't be executed if the configured authentication fails (when returning `null` in the authentication mechanism)
>
{type="note"}

## Naming the authentication provider

It is possible to give arbitrary names to the authentication providers you specify,
or to not provide a name at all (unnamed provider) by not setting the name argument or passing a null.

You cannot repeat authentication provider names, and you can define just one provider without a name.

In the case you repeat a name for the provider or try to define two unnamed providers, an exception will be thrown:

```text
java.lang.IllegalArgumentException: Provider with the name `authName` is already registered
```

Summarizing:

```kotlin
install(Authentication) {
    basic { // Unnamed `basic` provider
        // ...
    }
    form { // Unnamed `form` provider (exception, already defined a provider with name = null) 
        // ...
    }
    basic("name1") { // "name1" provider
        // ...
    }
    basic("name1") { // "name1" provider (exception, already defined a provider with name = "name1")
        // ...
    }
}
```

## Skipping/omitting authentication providers

You can also skip an authentication based on a criteria.

```kotlin
/**
 * Authentication filters specifying if authentication is required for particular [ApplicationCall]
 * If there is no filters, authentication is required. If any filter returns true, authentication is not required.
 */
fun AuthenticationProvider.skipWhen(predicate: (ApplicationCall) -> Boolean)
```

For example, to skip a basic authentication if there is already a session, you could write:

```kotlin
authentication {
    basic {
        skipWhen { call -> call.sessions.get<UserSession>() != null }
    }
}
```

## Advanced

>If you want to create custom authentication strategies,
>you can check the [Authentication feature](https://github.com/ktorio/ktor/tree/main/ktor-features/ktor-auth/jvm/src/io/ktor/auth) as a reference.
>The authentication feature defines two stages as part of its [Pipeline](https://github.com/ktorio/ktor/blob/main/ktor-features/ktor-auth/jvm/src/io/ktor/auth/AuthenticationPipeline.kt): `RequestAuthentication` and `CheckAuthentication`.
>
{type="note"}
