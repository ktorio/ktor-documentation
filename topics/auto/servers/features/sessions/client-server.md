[//]: # (title: Client/Server)
[//]: # (caption: Client/Server Sessions)
[//]: # (category: servers)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/sessions/client-server.html: - /features/sessions/client-server.html)
[//]: # (ktor_version_review: 1.0.0)

## Client-side/Server-side sessions (Session Content vs Session Id)
{ #client-server-cookies }

Ktor allows you to transfer either the session content or a session id.

Depending on the application, the size of the payload and the security, you might want to put the payload of
the session in the client or the server.

### Client-side sessions and transforms (Sending Session Content)
{ #client-cookies}

Without additional arguments for the `cookie` and `header` methods, the session is configured to keep the payload
at the client. And the full payload will be sent back and forth. In this mode, you can, and should apply
transforms to encrypt or authenticate sessions:

```kotlin
application.install(Sessions) {
    cookie<MySession>("SESSION") {
        val secretSignKey = hex("000102030405060708090a0b0c0d0e0f")
        transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
    }
} 
```

You should only use client-side sessions if your payload can't suffer from replay attacks. Also if you need to prevent
modifications, ensure that you are transforming the session with at least authentication, but ideally with encryption too.
This should prevent payload modification if you keep your secret key safe. But remember that if your key is compromised
and thus, you have to change the key, all the previous sessions will be marked invalid.
{ .note.security }

Client-side Sessions use transformers to manipulate the payload, for example to authenticate and/or encrypt it.

You can check the [transformers page](/servers/features/sessions/transformers.html) for a list of standard available transformers,
and for more information.

### Server-side sessions and storages (Sending Session Id)
{ #server-cookies }

If you specify storage, then the session will be configured to be stored on the server using that storage, and
a sessionId will be transferred between the server and the client instead of the full payload: 

```kotlin
application.install(Sessions) {
    cookie<MySession>("SESSION", storage = SessionStorageMemory())
} 
```

## Security examples for client-side sessions
{ #security }

If you plan to use client-side sessions, you need to understand the security implications it has. You have to keep
your secret hash/encryption keys safe, as if they are compromised, a person with the keys would potentially be able 
to impersonate any user. It is also a problem as then changing the key will invalidate all the sessions previously generated.

### Good usages for client-side cookies:

* **Storing user preferences, such as language, cookie acceptation and things like that.**

  No security concerns for this. Just preferences. If anyone could ever modify the session. No harm can be done at all.

* **Shopping cart information**

  If this information acts as a *wish list*, it would just be like preferences. No possible harm can be done here. 

* **Storing user login using a immutable user id or an email address.**

  Should be ok if at least authenticated (and with the knowledge of general risks) since in normal circumstances
  people won't be able to change it to impersonate another person. And if you store a unique immutable session id,
  using old session payloads, it would just give access to your own users who already have access. 

### Bad usages for client-side cookies:

* **Using session as cache. For example storing user's redeemable points.**

  If you are using a session as cache to prevent reading from a database, for example, *user points* that a user can
  use to purchase things. It is exploitable, since the user could purchase an item, but not to update the session
  or using an old session payload that would have more points.

* **Using session to store a mutable user name.**

  Consider if you are storing the user name in the session to keep login information. But also allow changing
  the username of an actual user. A malicious user could create an account, and rename its user several times
  storing valid session payloads for each user name. So if a new user is created using a previously renamed
  user name, the malicious user would have access to that account.
  Server-side sessions are also vulnerable to this, but the attacker would have to keep those sessions alive.

## Invalidating Client-side sessions
{ #invalidating-client-sessions }

Since client-side sessions can't be invalidated directly like server sessions. You can manually mark an expiration
time for the session by including an expiration timestamp as part of your session payload.

For example:

```kotlin
data class MyExpirableSession(val name: String, val expiration: Long)

fun Application.main() {
    routing {
        get("/user/panel") {
            val session = call.getMyExpirableSession()
            call.respondText("Welcome ${session.name}")
        }
    }
}

fun ApplicationCall.getMyExpirableSession(): MyExpirableSession {
    val session = sessions.get<MyExpirableSession>() ?: error("No session found")
    if (System.currentTimeMillis() > session.expiration) {
        error("Session expired")
    }
    return session
}
```