[//]: # (title: Storages)

Ktor allows you to store [session](sessions.md) data [on the server](client_server.md) and pass only a session ID between the server and the client. In this case, you can choose where to keep the payload on the server. 


## Built-in Storages {id="builtin_storages"}
The following storage types are available out-of-the-box:
* [SessionStorageMemory](https://api.ktor.io/%ktor_version%/io.ktor.sessions/-session-storage-memory/index.html) enables storing a session's content in memory. This storage keeps data while the server is running and discards information once the server stops. 
* [directorySessionStorage](https://api.ktor.io/%ktor_version%/io.ktor.sessions/directory-session-storage.html) can be used to store a session's data in a file under the specified directory.

To create the required storage type, pass it as a second parameter to the [cookie](cookie_header.md#cookie) or [header](cookie_header.md#header) method. For example, you can store cookies in the server memory as follows:

```kotlin
install(Sessions) {
    cookie<SampleSession>("SAMPLE_SESSION", storage = SessionStorageMemory())
}
```

To store cookies in a file under the `.sessions` directory, create the `directorySessionStorage` in this way:
```kotlin
import java.io.File
// ...
install(Sessions) {
    cookie<SampleSession>("SAMPLE_SESSION", storage = directorySessionStorage(File(".sessions")))
}
```


## Custom Storage {id="custom_storage"}

Ktor provides the [SessionStorage](https://api.ktor.io/%ktor_version%/io.ktor.sessions/-session-storage/index.html) interface that allows you to implement a custom storage. 
```kotlin
interface SessionStorage {
    suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit)
    suspend fun invalidate(id: String)
    suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R
}
```
All three functions are [suspending](https://kotlinlang.org/docs/composing-suspending-functions.html) and use [ByteWriteChannel](https://api.ktor.io/%ktor_version%/io.ktor.utils.io/-byte-write-channel/) and [ByteReadChannel](https://api.ktor.io/%ktor_version%/io.ktor.utils.io/-byte-read-channel/index.html) to read and write data from/to an asynchronous channel.
The example below shows how to implement the `SessionStorage` interface to store session data in a `ByteArray`:

```kotlin
```
{src="simplified-session-storage-sample.kt"}
