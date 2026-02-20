[//]: # (title: Digest authentication in Ktor Server)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-auth"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="auth-digest"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

The Digest authentication scheme is a part of the [HTTP framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) used for access control and authentication. In this scheme, a hash function is applied to a username and password before sending them over the network.

Ktor supports [RFC 7616](https://datatracker.ietf.org/doc/html/rfc7616) (HTTP Digest Access Authentication), which enhances the older RFC 2617 with modern security features including stronger hash algorithms, quality of protection options, and username hashing for privacy.

Ktor allows you to use digest authentication for logging in users and protecting specific [routes](server-routing.md). You can get general information about authentication in Ktor in the [](server-auth.md) section.

> Digest authentication provides stronger security than [Basic authentication](server-basic-auth.md) since passwords are never sent in clear text. However, you should still use [HTTPS/TLS](server-ssl.md) in production to protect against other attacks.

## Add dependencies {id="add_dependencies"}
To enable `digest` authentication, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Digest authentication flow {id="flow"}

The digest authentication flow looks as follows:

1. A client makes a request without the `Authorization` header to a specific [route](server-routing.md) in a server application.
2. A server responds to a client with a `401` (Unauthorized) response status and uses a `WWW-Authenticate` response header to provide information that the digest authentication scheme is used to protect a route. A typical `WWW-Authenticate` header looks like this:

   ```
   WWW-Authenticate: Digest
           realm="Access to the '/' path",
           nonce="e4549c0548886bc2",
           algorithm=SHA-512-256,
           qop="auth"
   ```
   {style="block"}

   In Ktor, you can specify the realm, supported algorithms, quality of protection, and the way of generating a nonce value when [configuring](#configure-provider) the `digest` authentication provider.

3. Usually a client displays a login dialog where a user can enter credentials. Then, a client makes a request with the following `Authorization` header:

   ```
   Authorization: Digest username="jetbrains",
           realm="Access to the '/' path",
           nonce="e4549c0548886bc2",
           uri="/",
           algorithm=SHA-512-256,
           qop=auth,
           nc=00000001,
           cnonce="0a4f113b",
           response="6629fae49393a05397450978507c4ef1"
   ```
   {style="block"}

   The `response` value is generated in the following way:

   a. `HA1 = H(username:realm:password)` where `H` is the configured hash algorithm (e.g., SHA-512-256)
   > This part [is stored](#digest-table) on a server and can be used by Ktor to validate user credentials.

   b. `HA2 = H(method:digestURI)` (for `qop=auth`) or `HA2 = H(method:digestURI:H(entityBody))` (for `qop=auth-int`)

   c. `response = H(HA1:nonce:nc:cnonce:qop:HA2)`

4. A server [validates](#configure-provider) credentials sent by a client and responds with the requested content. On successful authentication with QoP, the server also returns an `Authentication-Info` header for mutual authentication.


## Install digest authentication {id="install"}
To install the `digest` authentication provider, call the [digest](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/digest.html) function inside the `install` block:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
// ...
install(Authentication) {
    digest {
        // Configure digest authentication
    }
}
```
You can optionally specify a [provider name](server-auth.md#provider-name) that can be used to [authenticate a specified route](#authenticate-route).

## Configure digest authentication {id="configure"}

To get a general idea of how to configure different authentication providers in Ktor, see [](server-auth.md#configure). In this section, we'll see on configuration specifics of the `digest` authentication provider.

### Step 1: Choose hash algorithms {id="choose-algorithms"}

Ktor supports multiple hash algorithms for digest authentication. You can configure which algorithms your server accepts using the `algorithms` property:

| Algorithm        | Constant                           | Security Level  | Notes                                           |
|------------------|------------------------------------|-----------------|-------------------------------------------------|
| SHA-512-256      | `DigestAlgorithm.SHA_512_256`      | **Recommended** | Strongest security, use for new implementations |
| SHA-512-256-sess | `DigestAlgorithm.SHA_512_256_SESS` | **Recommended** | Session variant - includes client nonce in HA1  |
| SHA-256          | `DigestAlgorithm.SHA_256`          | Good            | Minimum recommended for production              |
| SHA-256-sess     | `DigestAlgorithm.SHA_256_SESS`     | Good            | Session variant - includes client nonce in HA1  |
| MD5              | `DigestAlgorithm.MD5`              | **Deprecated**  | Only for backward compatibility                 |
| MD5-sess         | `DigestAlgorithm.MD5_SESS`         | **Deprecated**  | Session variant - only for legacy compatibility |

```kotlin
install(Authentication) {
    digest("auth-digest") {
        realm = "Access to the '/' path"
        algorithms = listOf(DigestAlgorithm.SHA_512_256, DigestAlgorithm.MD5)
        // ...
    }
}
```

When multiple algorithms are configured, the server sends multiple `WWW-Authenticate` headers, allowing clients to choose the strongest algorithm they support.

> The default algorithms are `SHA-512-256` and `MD5` (for backward compatibility with older clients).

#### Session algorithms (-sess variants) {id="sess-algorithms"}

The `-sess` algorithm variants (e.g., `SHA-512-256-sess`, `SHA-256-sess`, `MD5-sess`) modify how the `HA1` hash is computed. Instead of storing `H(username:realm:password)`, session algorithms compute `H(H(username:realm:password):nonce:cnonce)`, where `cnonce` is client-provided nonce.

**Benefits:**
- The session-specific hash prevents pre-computed dictionary attacks
- Compromising one session's hash doesn't reveal the password or help with other sessions

**Drawback:**
- The server must compute the hash for each authentication request (cannot use pre-computed values)

For most applications, the standard (non-sess) algorithms are enough, especially when combined with strong hash functions like SHA-512-256.

### Step 2: Provide a user table with digests {id="digest-table"}

The `digest` authentication provider validates user credentials using the `HA1` part of a digest message. So, you can provide a user table that contains usernames and corresponding `HA1` hashes.

Since different algorithms produce different hash values, you need to store the appropriate hash for each algorithm you support or compute the hash dynamically based on the algorithm requested by the client:

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/authdigest/Application.kt" include-lines="11-18"}


### Step 3: Configure a digest provider {id="configure-provider"}

The `digest` authentication provider exposes its settings via the [DigestAuthenticationProvider.Config](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-digest-authentication-provider/-config/index.html) class. In the example below, the following settings are specified:
* The `realm` property sets the realm to be passed in the `WWW-Authenticate` header.
* The `algorithms` property specifies which hash algorithms to accept.
* The `digestProvider` function fetches the `HA1` part of digest for a specified username and algorithm.
* (Optional) The `validate` function allows you to map the credentials to a custom principal.

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/authdigest/Application.kt" include-lines="20-40,50-52"}

The `digestProvider` function receives three parameters:
- `userName` - the username from the client's request
- `realm` - the configured realm
- `algorithm` - the hash algorithm the client is using

You should return the `HA1` hash computed with the specified algorithm, or `null` if the user is not found.

You can also use the [nonceManager](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-digest-authentication-provider/-config/nonce-manager.html) property to specify how to generate nonce values.

### Step 4: Configure Quality of Protection {id="configure-qop"}

Quality of Protection (QoP) determines what is included in the digest calculation:

- `DigestQop.AUTH` - Authentication only (default). The digest includes the request method and URI.
- `DigestQop.AUTH_INT` - Authentication with integrity protection. The digest also includes the request body, providing protection against tampering.

```kotlin
install(Authentication) {
    digest("auth-digest") {
        realm = "Secure API"
        supportedQop = listOf(DigestQop.AUTH, DigestQop.AUTH_INT)
        // ...
    }
}
```

> When using `auth-int`, the request body is consumed during authentication. If you need to access the body in your route handler, install the [DoubleReceive](server-double-receive.md) plugin.


### Step 5: Protect specific resources {id="authenticate-route"}

After configuring the `digest` provider, you can protect specific resources in our application using the **[authenticate](server-auth.md#authenticate-route)** function. In the case of successful authentication, you can retrieve an authenticated [Principal](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-principal/index.html) inside a route handler using the `call.principal` function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-digest/src/main/kotlin/authdigest/Application.kt" include-lines="41-49"}


## Advanced configuration {id="advanced"}

### User hash support {id="userhash"}

RFC 7616 introduces username hashing (`userhash`) for privacy protection. When enabled, clients can send a hashed version of the username instead of the plaintext username.

To support username hashing, configure a `userHashResolver`:

```kotlin
val users = listOf("alice", "bob", "charlie")

install(Authentication) {
    digest("auth-digest") {
        realm = "Private API"
        userHashResolver { userhash, realm, algorithm ->
            // Find the actual username from the hash
            users.find { username ->
                val digester = algorithm.toDigester()
                val computedHash = hex(digester.digest("$username:$realm".toByteArray()))
                computedHash == userhash
            }
        }
        digestProvider { userName, realm, algorithm ->
            // ...
        }
    }
}
```

When `userHashResolver` is configured, the server advertises `userhash=true` in the `WWW-Authenticate` challenge header.

### Strict RFC 7616 mode {id="strict-mode"}

For maximum security in new applications without legacy client requirements, use `strictRfc7616Mode()`:

```kotlin
install(Authentication) {
    digest("auth-digest") {
        realm = "Secure Zone"
        strictRfc7616Mode()
        digestProvider { userName, realm, algorithm ->
            // algorithm will never be MD5 in strict mode
        }
    }
}
```

Strict mode:
- Removes MD5 algorithms (only allows SHA-256 and SHA-512-256)
- Enforces UTF-8 charset

### UTF-8 charset support {id="charset"}

The implementation supports UTF-8 charset for usernames and passwords containing international characters:

```kotlin
install(Authentication) {
    digest("auth-digest") {
        realm = "My App"
        charset = Charsets.UTF_8  // This is the default
        // ...
    }
}
```

### Authentication-Info header {id="auth-info"}

On successful authentication with QoP, the server automatically returns an `Authentication-Info` header containing:
- `rspauth` - Response authentication value for mutual authentication
- `nextnonce` - Next nonce for the client to use
- `qop`, `nc`, `cnonce` - Echo of authentication parameters

This allows clients to verify the server's identity (mutual authentication).


## Security recommendations {id="security"}

1. **Use SHA-512-256 or SHA-256** - Avoid MD5 in production; it's only included for legacy compatibility.

2. **Use `strictRfc7616Mode()`** - For new applications without legacy client requirements.

3. **Implement proper nonce management** – Use a custom `NonceManager` to prevent replay attacks in distributed environments.

4. **Consider `auth-int`** - When request body integrity is important for your application.

5. **Enable `userhash`** - For privacy protection of usernames.

6. **Always use HTTPS** – Digest authentication alone doesn't encrypt traffic; always use TLS in production.


## Migration from legacy digest auth {id="migration"}

If you're upgrading from an older Ktor version (3.4.0 or older):

### Before (Legacy)
```kotlin
install(Authentication) {
    digest("auth") {
        realm = "MyRealm"
        algorithmName = "MD5"  // Deprecated property
        digestProvider { userName, realm ->
            // Old signature without algorithm parameter
            getMd5Digest("$userName:$realm:$password")
        }
    }
}
```

### After (RFC 7616)
```kotlin
install(Authentication) {
    digest("auth") {
        realm = "MyRealm"
        // Support both modern and legacy clients
        algorithms = listOf(DigestAlgorithm.SHA_512_256, DigestAlgorithm.MD5)
        digestProvider { userName, realm, algorithm ->
            // New signature receives the algorithm
            val password = getPassword(userName) ?: return@digestProvider null
            algorithm.toDigester().digest("$userName:$realm:$password".toByteArray())
        }
    }
}
```

### Deprecation notes

The following APIs are deprecated:
- `algorithmName: String` property - Use `algorithms: List<DigestAlgorithm>` instead
- Old `digestProvider { userName, realm -> }` signature - Still works but the new signature with algorithm parameter is preferred
- `DigestAlgorithm.MD5` and `DigestAlgorithm.MD5_SESS` - Deprecated for security reasons, avoid it in production
