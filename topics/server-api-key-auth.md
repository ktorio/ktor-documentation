[//]: # (title: API Key authentication)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-auth-api-key"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-auth</code>, <code>io.ktor:%artifact_name%</code>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

API Key authentication is a simple authentication method where clients pass a secret key as part of their request,
typically in a header. This key serves as both the identifier and the authentication mechanism.

Ktor allows you to use API Key authentication for securing [routes](server-routing.md) and validating client requests.

> You can get general information about authentication in Ktor in the [](server-auth.md) section.

> API Keys should be kept secret and transmitted securely. It's recommended to use [HTTPS/TLS](server-ssl.md) to protect
> API keys in transit.
>
{style="note"}

## Add dependencies {id="add_dependencies"}

To enable API Key authentication, add the `ktor-server-auth` and `%artifact_name%` artifacts in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## API Key authentication flow {id="flow"}

The API Key authentication flow looks as follows:

1. A client makes a request with an API key included in a header (typically `X-API-Key`) to a
   specific [route](server-routing.md) in a server application.
2. The server validates the API key using custom validation logic.
3. If the key is valid, the server responds with the requested content. If the key is invalid or missing, the server
   responds with a `401 Unauthorized` status.

## Install API Key authentication {id="install"}

To install the `apiKey` authentication provider, call
the [`apiKey`](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/api-key.html) function inside the
`install(Authentication)` block:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
// ...
install(Authentication) {
    apiKey {
        // Configure API Key authentication
    }
}
```

You can optionally specify a [provider name](server-auth.md#provider-name) that can be used
to [authenticate a specified route](#authenticate-route).

## Configure API Key authentication {id="configure"}

To get a general idea of how to configure different authentication providers in Ktor, see [](server-auth.md#configure).
In this section, we'll see the configuration specifics of the `apiKey` authentication provider.

### Step 1: Configure an API Key provider {id="configure-provider"}

The `apiKey` authentication provider exposes its settings via
the [ApiKeyAuthenticationProvider.Config](https://api.ktor.io/ktor-server-auth/io.ktor.server.auth/-api-key-authentication-provider/-config/index.html)
class. In the example below, the following settings are specified:

* The `validate` function receives the API key extracted from the request and returns a `Principal` in the case of
  successful authentication or `null` if authentication fails.

Here's a minimal example:

```kotlin
data class AppPrincipal(val key: String) : Principal

install(Authentication) {
    apiKey {
        validate { keyFromHeader ->
            val expectedApiKey = "this-is-expected-key"
            keyFromHeader
                .takeIf { it == expectedApiKey }
                ?.let { AppPrincipal(it) }
        }
    }
}
```

#### Customize key location {id="key-location"}

By default, the `apiKey` provider looks for the API key in the `X-API-Key` header.

You can use `headerName` to specify a custom header:

```kotlin
apiKey("api-key-header") {
    headerName = "X-Secret-Key"
    validate { key ->
        // Validate and return principal
    }
}
```

### Step 2: Validate API keys {id="validate"}

The validation logic depends on your application's requirements. Here are common approaches:

#### Static key comparison {id="static-key"}

For simple cases, you can compare against a predefined key:

```kotlin
apiKey {
    validate { keyFromHeader ->
        val expectedApiKey = environment.config.property("api.key").getString()
        keyFromHeader
            .takeIf { it == expectedApiKey }
            ?.let { AppPrincipal(it) }
    }
}
```

> Store sensitive API keys in configuration files or environment variables, not in source code.
>
{style="note"}

#### Database lookup {id="database-lookup"}

For multiple API keys, validate against a database:

```kotlin
apiKey {
    validate { keyFromHeader ->
        // Look up the key in database
        val user = database.findUserByApiKey(keyFromHeader)
        user?.let { UserIdPrincipal(it.username) }
    }
}
```

#### Multiple validation criteria {id="multiple-criteria"}

You can implement complex validation logic:

```kotlin
apiKey {
    validate { keyFromHeader ->
        val apiKey = database.findApiKey(keyFromHeader)

        // Check if key exists, is active, and not expired
        if (apiKey != null &&
            apiKey.isActive &&
            apiKey.expiresAt > Clock.System.now()
        ) {
            UserIdPrincipal(apiKey.userId)
        } else {
            null
        }
    }
}
```

### Step 3: Configure challenge {id="challenge"}

You can customize the response sent when authentication fails using the `challenge` function:

```kotlin
apiKey {
    validate { key ->
        // Validation logic
    }
    challenge { defaultScheme, realm ->
        call.respond(
            HttpStatusCode.Unauthorized,
            "Invalid or missing API key"
        )
    }
}
```

### Step 4: Protect specific resources {id="authenticate-route"}

After configuring the `apiKey` provider, you can protect specific resources in your application using the
[`authenticate`](server-auth.md#authenticate-route) function. In the case of successful authentication, you can
retrieve an authenticated principal inside a route handler using the `call.principal` function.

```kotlin
routing {
    authenticate {
        get("/") {
            val principal = call.principal<AppPrincipal>()!!
            call.respondText("Hello, authenticated client! Your key: ${principal.key}")
        }
    }
}
```

## Complete example {id="complete-example"}

Here's a complete minimal example of API Key authentication:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class AppPrincipal(val key: String) : Principal

fun Application.module() {
    val expectedApiKey = "this-is-expected-key"

    install(Authentication) {
        apiKey {
            validate { keyFromHeader ->
                keyFromHeader
                    .takeIf { it == expectedApiKey }
                    ?.let { AppPrincipal(it) }
            }
        }
    }

    routing {
        authenticate {
            get("/") {
                val principal = call.principal<AppPrincipal>()!!
                call.respondText("Key: ${principal.key}")
            }
        }
    }
}
```

## Best practices {id="best-practices"}

When implementing API Key authentication, consider the following best practices:

1. **Use HTTPS**: Always transmit API keys over HTTPS to prevent interception.
2. **Store securely**: Never hardcode API keys in source code. Use environment variables or secure configuration
   management.
3. **Key rotation**: Implement a mechanism for rotating API keys periodically.
4. **Rate limiting**: Combine API Key authentication with rate limiting to prevent abuse.
5. **Logging**: Log authentication failures for security monitoring, but never log the actual API keys.
6. **Key format**: Use cryptographically secure random strings for API keys (e.g., UUID, base64-encoded random bytes).
7. **Multiple keys**: Consider supporting multiple API keys per user for different applications or purposes.
8. **Expiration**: Implement key expiration for enhanced security.
