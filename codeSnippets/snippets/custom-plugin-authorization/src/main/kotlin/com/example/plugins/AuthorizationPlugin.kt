package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

val AuthorizationPlugin = createRouteScopedPlugin(
    name = "AuthorizationPlugin",
    createConfiguration = ::PluginConfiguration
) {
    val roles = pluginConfig.roles
    val getRole = pluginConfig.getRole
    pluginConfig.apply {
        on(AuthenticationChecked) { call ->
            val userName = call.principal<UserIdPrincipal>()?.name
            val userRole = getRole(userName)
            if (userRole !in roles) {
                call.respondText("You are not allowed to visit this page", status = HttpStatusCode.Forbidden)
            }
        }
    }
}

class PluginConfiguration {
    var roles: Set<String> = emptySet()
    lateinit var getRole : (userName: String?) -> String
}
