[//]: # (title: Digest)

<microformat>
<var name="example_name" value="auth-digest"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<include src="lib.xml" include-id="outdated_warning"/>

## Add dependencies {id="add_dependencies"}
To enable `digest` authentication, you need to include the `ktor-auth` artifact in the build script:
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Usage {id="usage"}

Ktor supports [HTTP digest authentication](https://en.wikipedia.org/wiki/Digest_access_authentication).
It works differently than the basic/form auths:

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/com/example/Application.kt" lines="10-16,18-26,34"}


Instead of providing a verifier, you have to provide a `userTable` that is in charge of
returning the `HA1` part of the digest. In the case of `MD5`: `MD5("$username:$realm:$password")`.
The idea is that [you can store passwords already hashed](https://tools.ietf.org/html/rfc2069#section-3.5).
And only return the expected hash for a specific user, or *null* if the user does not exist.
The callback is suspendable, so you can retrieve or compute the expected hash asynchronously,
for example from disk or a database.

`HA1` (`H(A1)`) comes from [RFC 2069 (An Extension to HTTP: Digest Access Authentication)](https://tools.ietf.org/html/rfc2069)  

```text
HA1=MD5(username:realm:password) <-- You usually store this.
HA2=MD5(method:digestURI)
response=MD5(HA1:nonce:HA2) <-- The client and the server sends and checks this.
```

>While `realm` is guaranteed to be the `realm` passed to the `digestAuthentication` function and it is passed just for convenience,
>`userName` *can* be any value, so take this into account and remember to escape and or validate it, when using that value
>for accessing the file system, accessing databases, storing it, generating HTML, etc.
>
{type="note"}