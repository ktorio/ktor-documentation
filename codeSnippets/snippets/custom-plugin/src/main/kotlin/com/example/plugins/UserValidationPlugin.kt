package com.example.plugins

import io.ktor.server.application.*

val UserValidationPlugin = createRouteScopedPlugin("UserValidationPlugin") {
    onCallValidators { call ->
        val principal = call.principal<UserIdPrincipal>()

        if (principal != null) {
            application.log.info("Validating request for ${principal.name}")
        }
    }
}