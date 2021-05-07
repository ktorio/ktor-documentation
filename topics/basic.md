[//]: # (title: Basic)

<include src="lib.md" include-id="outdated_warning"/>

<microformat>
<p>Code examples:</p>
<p><a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-basic">auth-basic</a></p>
<p><a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-basic-hash-table">auth-basic-hash-table</a></p>
</microformat>

## Add dependencies {id="add_dependencies"}
To enable the `basic` authentication, you need to include the `ktor-auth` artifact in the build script:
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Usage {id="usage"}

The `basic` authentication uses a username and password as credentials:

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="9-20"}

The `validate` method provides a callback that must generate a Principal from given a `UserPasswordCredential`
or null for invalid credentials. That callback is marked as *suspending*, so that you can validate credentials in an asynchronous fashion.

You can use several strategies for validating:

### Manual credential validation

Since there is the `validate` callback for authentication, you can just put your code there.
So you can do things like checking the password against a constant, authenticating using a database
or composing several validation mechanisms.

```kotlin
```
{src="snippets/auth-basic/src/main/kotlin/com/example/Application.kt" lines="9-20"}

Remember that both the `name` and the `password` from the credentials are arbitrary values.
Remember to escape and/or validate them when accessing with those values to the file system, a database,
when storing them, or generating HTML with its content, etc.
{ .security.note }

### Validating using UserHashedTableAuth

There is a class that handles hashed passwords in-memory to authenticate `UserPasswordCredential`.
You can populate it from constants in code or from another source. You can use predefined digest functions
or your own.

*Instantiating:*

```kotlin
```
{src="snippets/auth-basic-hash-table/src/main/kotlin/com/example/Application.kt" lines="9-16"}


*Configuring server/routes:*

```kotlin
```
{src="snippets/auth-basic-hash-table/src/main/kotlin/com/example/Application.kt" lines="19-26"}

>The idea here is that you are not storing the actual password but a hash, so even if your data source is leaked,
>the passwords are not directly compromised. Though keep in mind that when using poor passwords and weak hashing algorithms
>it is possible to do brute-force attacks. You can append (instead of prepend) long salt values and do multiple hash
>stages or do key derivate functions to increase security and make brute-force attacks non-viable.
>You can also enforce or encourage strong passwords when creating users.
>
{type="note"}