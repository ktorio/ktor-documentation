package io.ktor.application.newapi.samples

import io.ktor.application.*
import io.ktor.application.newapi.KtorFeature.Companion.makeFeature
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.*

/**
 * Redirect non-secure requests to HTTPS
 */
val HttpsRedirect = makeFeature("HttpsRedirect", { HttpsRedirectConfig() }) {
    // `onCall` callback defines how feature should change the app's behaviour when the call is
    // being processed.
    onCall { call ->
        if (call.request.origin.scheme == "http" &&
            feature.excludePredicates.none { predicate -> predicate(call) }
        ) {
            val redirectUrl = call.url { protocol = URLProtocol.HTTPS; port = feature.sslPort }
            call.respondRedirect(redirectUrl, feature.permanentRedirect)

            // `finish` function can be called inside `makeFeature` block any time.
            // It finishes (i.e. stops) the process of handling this call.
            // Nothing will be applied to the call after calling this function.
            finish()
        }
    }
}

/**
 * Redirect feature configuration
 */
class HttpsRedirectConfig {

    /**
     * HTTPS port (443 by default) to redirect to
     */
    public var sslPort: Int = URLProtocol.HTTPS.defaultPort

    /**
     * Use permanent redirect or temporary
     */
    public var permanentRedirect: Boolean = true

    /**
     * The list of call predicates for redirect exclusion.
     * Any call matching any of the predicates will not be redirected by this feature.
     */
    @KtorExperimentalAPI
    public val excludePredicates: MutableList<(ApplicationCall) -> Boolean> = ArrayList()

    /**
     * Exclude calls with paths matching the [pathPrefix] from being redirected to https by this feature.
     */
    @KtorExperimentalAPI
    public fun excludePrefix(pathPrefix: String) {
        exclude { call ->
            call.request.origin.uri.startsWith(pathPrefix)
        }
    }

    /**
     * Exclude calls matching the [predicate] from being redirected to https by this feature.
     */
    @KtorExperimentalAPI
    public fun exclude(predicate: (call: ApplicationCall) -> Boolean) {
        excludePredicates.add(predicate)
    }
}