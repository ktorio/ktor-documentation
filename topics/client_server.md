[//]: # (title: Client/Server)

In Ktor, you can manage the [session](sessions.md) data in two ways:
- Pass session data between the server and the client. In this case, you can specify how to [serialize](serializers.md) and [transform](transformers.md) the payload to sign or encrypt data sent to the client.
- Store session data on the server and pass only a session ID between the server and the client. In such a case, you can choose [where to store the payload](storages.md) on the server. For example, you can store session data in memory, in a specified folder, or Redis. If necessary, you can implement your own custom storage.

## Client {id="client"}

If you pass only the session name to the [cookie or header method](cookie_header.md), session data will be passed between the client and server. In this case, it is recommended to [sign or encrypt](transformers.md) data for security reasons, for example:
```kotlin
install(Sessions) {
    cookie<LoginSession>("LOGIN_SESSION") {
        val secretSignKey = hex("000102030405060708090a0b0c0d0e0f")
        transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
    }
}
```

### Invalidate client-side sessions
Client-side sessions can't be invalidated directly like server sessions. You can pass a session expiration time as a part of your session payload:
```kotlin
data class ExpirableLoginSession(val username: String, val expiration: Long)
```
Then, configure a session inside the [install](sessions.md#install) block:
```kotlin
install(Sessions) {
    cookie<ExpirableLoginSession>("EXPIRABLE_LOGIN_SESSION")
}
```
Finally, you can [get the session content](sessions.md#set-content) and check whether the current time exceeds the specified expiration time:
```kotlin
routing {
    get("/profile") {
        val session = call.sessions.get<ExpirableLoginSession>() ?: error("Session not found")
        if (System.currentTimeMillis() > session.expiration) {
            error("Session expired")
        }
    }
}
```



## Server {id="server"}
Ktor provides different storage types for storing session data on the server. For example, you can use the [SessionStorageMemory](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-sessions/io.ktor.server.sessions/-session-storage-memory/index.html) in-memory storage for development purposes:
```kotlin
install(Sessions) {
    cookie<LoginSession>("LOGIN_SESSION", storage = SessionStorageMemory())
}
```
You can learn more about the available storages from [](storages.md).
