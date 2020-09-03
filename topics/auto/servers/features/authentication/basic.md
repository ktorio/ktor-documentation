[//]: # (title: Basic and Form)
[//]: # (caption: Basic and Form authentication)
[//]: # (category: servers)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/authentication/basic.html: - /features/authentication/basic.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor supports two methods of authentication with the user and raw password as credentials:
`basic` and `form`.

```kotlin
install(Authentication) {
    basic(name = "myauth1") {
        realm = "Ktor Server"
        validate { credentials -> /*...*/ }
    }

    form(name = "myauth2") {
        userParamName = "user"
        passwordParamName = "password"
        challenge = FormAuthChallenge.Unauthorized
        validate { credentials -> /*...*/ }
    }
}
```

Both authentication providers have a method `validate` to provide a callback that must generate a Principal from given a `UserPasswordCredential`
or null for invalid credentials. That callback is marked as *suspending*, so that you can validate credentials in an asynchronous fashion.

You can use several strategies for validating:

## Strategy: Manual credential validation

Since there is a validate callback for authentication, you can just put your code there.
So you can do things like checking the password against a constant, authenticating using a database
or composing several validation mechanisms.

```kotlin
application.install(Authentication) {
    basic("authName") {
        realm = "ktor"
        validate { credentials ->
            if (credentials.password == "${credentials.name}123") UserIdPrincipal(credentials.name) else null
        }
    }
}
```

Remember that both the `name` and the `password` from the credentials are arbitrary values.
Remember to escape and/or validate them when accessing with those values to the file system, a database,
when storing them, or generating HTML with its content, etc.
{ .security.note }

## Strategy: Validating using UserHashedTableAuth

There is a class that handles hashed passwords in-memory to authenticate `UserPasswordCredential`.
You can populate it from constants in code or from another source. You can use predefined digest functions
or your own.

*Instantiating:*

```kotlin
val userTable = UserHashedTableAuth(getDigestFunction("SHA-256", salt = "ktor"), mapOf(
    "test" to decodeBase64("VltM4nfheqcJSyH887H+4NEOm2tDuKCl83p5axYXlF0=") // sha256 for "test"
))
```

*Configuring server/routes:*

```kotlin
application.install(Authentication) {
    basic("authName") {
        realm = "ktor"
        authenticate { credentials -> userTable.authenticate(credentials) }
    }
}
```

>The idea here is that you are not storing the actual password but a hash, so even if your data source is leaked,
>the passwords are not directly compromised. Though keep in mind that when using poor passwords and weak hashing algorithms
>it is possible to do brute-force attacks. You can append (instead of prepend) long salt values and do multiple hash
>stages or do key derivate functions to increase security and make brute-force attacks non-viable.
>You can also enforce or encourage strong passwords when creating users.
>
{type="note"}