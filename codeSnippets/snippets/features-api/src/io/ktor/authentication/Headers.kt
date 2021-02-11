// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.authentication

import io.ktor.http.auth.*
import io.ktor.request.*

/**
 * Parses an authorization header from a [ApplicationRequest] returning a [HttpAuthHeader].
 */
public fun ApplicationRequest.parseAuthorizationHeader(): HttpAuthHeader? = authorization()?.let {
    parseAuthorizationHeader(it)
}
