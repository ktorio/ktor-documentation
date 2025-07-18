[//]: # (title: What's new in Ktor 3.1.0)

<show-structure for="chapter,procedure" depth="2"/>

_[Released: February 11, 2025](releases.md#release-details)_

## Ktor Server

### Deferred session retrieval

Ktor 3.1.0 introduces a new system property to enable lazy session loading. This allows sessions to be read only when
accessed, rather than at the start of every request.

You can now defer session loading by enabling the `io.ktor.server.sessions.deferred` system property:

```kotlin
System.setProperty("io.ktor.server.sessions.deferred", "true")
```

This change eliminates unnecessary session I/O for unauthenticated and static routes, and will become the default in
the next major release.