[//]: # (title: Digest)

<include src="lib.md" include-id="outdated_warning"/>

Ktor supports [HTTP digest authentication](https://en.wikipedia.org/wiki/Digest_access_authentication).
It works differently than the basic/form auths:

```kotlin
authentication {
    digest {
        val password = "Circle Of Life"
        digester = MessageDigest.getInstance("MD5")
        realm = "testrealm@host.com"
        userNameRealmPasswordDigestProvider = { userName, realm ->
            when (userName) {
                "missing" -> null
                else -> {
                    digester.reset()
                    digester.update("$userName:$realm:$password".toByteArray())
                    digester.digest()
                }
            }
        }
    }
}
```

Instead of providing a verifier, you have to provide a `userNameRealmPasswordDigestProvider` that is in charge of
returning the `HA1` part of the digest. In the case of `MD5`: `MD5("$username:$realm:$password")`.
The idea is that [you can store passwords already hashed](https://tools.ietf.org/html/rfc2069#section-3.5).
And only return the expected hash for a specific user, or *null* if the user does not exist.
The callback is suspendable, so you can retrieve or compute the expected hash asynchronously,
for example from disk or a database.

```kotlin
authentication {
    val myRealm = "MyRealm"
    val usersInMyRealmToHA1: Map<String, ByteArray> = mapOf(
        // pass="test", HA1=MD5("test:MyRealm:pass")="fb12475e62dedc5c2744d98eb73b8877"
        "test" to hex("fb12475e62dedc5c2744d98eb73b8877")
    )

    digest("auth") {
        userNameRealmPasswordDigestProvider = { userName, realm ->
            usersInMyRealmToHA1[userName]
        }
    }
}
```


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