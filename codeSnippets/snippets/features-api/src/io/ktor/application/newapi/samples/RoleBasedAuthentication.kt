package io.ktor.application.newapi.samples

import io.ktor.application.newapi.KtorFeature.Companion.makeFeature
import io.ktor.authentication.*
import io.ktor.request.*

val RoleBasedAuthorization = makeFeature("RoleBasedAuthorization", { RBAuthConfig() }) {
    // Following statement guaranties that all the extensions will be executed only after
    // same extensions are already executed in Authentication feature:
    afterFeature(Authentication) {
        onCall { call ->
            // Principal taken from Authentication feature can be used for retrieving a custom role
            val principal = call.authentication.principal<Principal>()
                ?: throw AuthorizationException("Missing principal")

            val roles = feature.getRoles(principal)
            val role = call.request.header("Role")
            if (!roles.contains(role)) {
                throw AuthorizationException("Principal lacks required role ${role}")
            }
        }
    }
}

// Somewhere defined (`RBAuthConfig` stands for Role Based Authentication Configuration):
class RBAuthConfig {
    var getRoles: (Principal) -> Set<String> = { emptySet() }
}

class AuthorizationException(message: String): Exception(message)

