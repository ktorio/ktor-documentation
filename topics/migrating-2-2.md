[//]: # (title: Migrating from 2.0.x to 2.2.x)

<show-structure for="chapter" depth="2"/>

This guide provides instructions on how to migrate your Ktor application from the 2.0.x version to 2.2.x.

> The API marked with the `WARNING` deprecation level will continue working till the 3.0.0 release.
> You can learn more about deprecation levels from [Deprecated](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-deprecated/).

## Ktor Server {id="server"}

### Cookies {id="cookies"}
With v2.2.0, the following API members related to configuring [response cookies](server-responses.md#cookies) are changed:
- The `maxAge` parameter type passed to the `append` function is changed from `Int` to `Long`.
- The `appendExpired` function is deprecated. Use the `append` function with the `expires` parameter instead.



### Request address information {id="request-address-info"}

Starting with the 2.2.0 version, the `RequestConnectionPoint.host` and `RequestConnectionPoint.port` properties 
used to get the hostname/port to which the request was made are deprecated.

```kotlin
get("/hello") {
    val originHost = call.request.origin.host
    val originPort = call.request.origin.port
}

```

Use `RequestConnectionPoint.serverHost` and `RequestConnectionPoint.serverPort` instead.
We've also added the `localHost`/`localPort` properties that return the hostname/port on which the request was received. 
You can learn more from the [](server-forward-headers.md#original-request-information).


### Merging configurations {id="merge-configs"}
Before v2.2.0, the `List<ApplicationConfig>.merge()` function is used to merge application configurations.
In case both configurations have the same key, the resulting configuration takes the value from the first one.
With this release, the following API is introduced to improve this behavior:
- `public fun ApplicationConfig.withFallback(other: ApplicationConfig): ApplicationConfig`: this function works the same way as `merge()` and takes the value from the first configuration.
- `public fun ApplicationConfig.mergeWith(other: ApplicationConfig): ApplicationConfig`: the resulting configuration takes the value from the second one.


## Ktor Client {id="client"}

### Caching: Persistent storage {id="persistent-storage"}

With v2.2.0, the following API related to response [caching](client-caching.md) is deprecated:
- The `HttpCacheStorage` class is replaced with the `CacheStorage` interface, which can be used to implement a persistent storage for the required platform.
- The `publicStorage`/`privateStorage` properties are replaced with corresponding functions that accept `CacheStorage` instances.


### Custom plugins {id="custom-plugins"}

Starting with the 2.2.0 release, Ktor provides a new API for creating custom client plugins. 
To learn more, see [](client-custom-plugins.md).


## New memory model {id="new-mm"}

With v2.2.0, Ktor uses the 1.7.20 version of Kotlin, in which the new Kotlin/Native memory model 
is [enabled by default](https://kotlinlang.org/docs/whatsnew1720.html#the-new-kotlin-native-memory-manager-enabled-by-default).
This means that you don't need to enable it explicitly for [Native server](server-native.md) or client engines targeting [Kotlin/Native](client-engines.md#native).
