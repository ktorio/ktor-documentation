[//]: # (title: Cookie/Header)
[//]: # (caption: Cookie/Header Sessions)
[//]: # (category: servers)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/sessions/cookie-header.html: - /features/sessions/cookie-header.html)
[//]: # (ktor_version_review: 1.0.0)

You can either use cookies or custom HTTP headers for sessions. The code is roughly the same but you have to
call either the `cookie` or `header` method, depending on where you want to send the session information.

## Cookies vs Headers sessions
{id="cookies-headers "}

Depending on the consumer, you might want to transfer the sessionId or the payload using a cookie,
or a header. For example, for a website, you will normally use cookies, while for an API you might want to use headers.

The Sessions.Configuration provide two methods `cookie` and `header` to select how to transfer the sessions: 

### Cookies

```kotlin
application.install(Sessions) {
    cookie<MySession>("SESSION")
} 
```

You can configure the cookie by providing an additional block. There is a cookie property allowing
to configure it, for example by adding a [SameSite extension](https://caniuse.com/#search=samesite):

```kotlin
application.install(Sessions) {
    cookie<MySession>("SESSION") {
        cookie.extensions["SameSite"] = "lax"
    }
} 
```

The Cookie method is intended for browser sessions. It will use a standard
[`Set-Cookie` header](https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Set-Cookie).
Inside the cookie block, you have access to a `cookie` property which allows you to configure the `Set-Cookie` header,
for example, by setting a cookie's `path` or expiration, domain or https related things.

```kotlin
install(Sessions) {
    cookie<SampleSession>("COOKIE_NAME") {
        cookie.path = "/"
        /* ... */
    }
}
```

### Headers

The Header method is intended for APIs, both for using in JavaScript XHR requests and for requesting them
from the server side. It is usually easier for API clients to read and generate custom headers than to handle
cookies.

```kotlin
install(Sessions) {
    header<SampleSession>("HTTP_HEADER_NAME") { /* ... */ }
}
```

```kotlin
application.install(Sessions) {
    header<MySession>("SESSION")
} 
```

## Custom storages
{id="extending-storages"}

The Sessions API provides a `SessionStorage` interface, that looks like this:

```kotlin
interface SessionStorage {
    suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit)
    suspend fun invalidate(id: String)
    suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R
}
```

All three functions are marked as `suspend` and are designed to be fully asynchronous
and use `ByteWriteChannel` and `ByteReadChannel` from `kotlinx.coroutines.io` that provide
APIs for reading and writing from an asynchronous Channel.

In your implementations, you have to call the callbacks providing a ByteWriteChannel and a ByteReadChannel
that you have to provide: it is your responsibility to open and close them.
You can read more about `ByteWriteChannel` and `ByteReadChannel` in their libraries documentation.
If you just need to load or store a ByteArray, you can use this snippet which provides a simplified session storage:

{% include simplified-session-storage-sample.md %}

{% include tabbed-code.html
    tab1-title="SimplifiedSessionStorage.kt" tab1-content=simplified-session-storage-sample-kt
%}

With this simplified storage you only have to implement two simpler methods:

```kotlin
abstract class SimplifiedSessionStorage : SessionStorage {
    abstract suspend fun read(id: String): ByteArray?
    abstract suspend fun write(id: String, data: ByteArray?): Unit
}
```

So for example, a redis session storage would look like this:

```kotlin
class RedisSessionStorage(val redis: Redis, val prefix: String = "session_", val ttlSeconds: Int = 3600) :
    SimplifiedSessionStorage() {
    private fun buildKey(id: String) = "$prefix$id"

    override suspend fun read(id: String): ByteArray? {
        val key = buildKey(id)
        return redis.get(key)?.unhex?.apply {
            redis.expire(key, ttlSeconds) // refresh
        }
    }

    override suspend fun write(id: String, data: ByteArray?) {
        val key = buildKey(id)
        if (data == null) {
            redis.del(buildKey(id))
        } else {
            redis.set(key, data.hex)
            redis.expire(key, ttlSeconds)
        }
    }
}
```