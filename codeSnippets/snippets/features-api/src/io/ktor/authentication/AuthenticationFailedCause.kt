// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.authentication

/**
 * Represents a cause for authentication challenge request
 */
public sealed class AuthenticationFailedCause {
    /**
     * Represents a case when no credentials were provided
     */
    public object NoCredentials : AuthenticationFailedCause()

    /**
     * Represents a case when invalid credentials were provided
     */
    public object InvalidCredentials : AuthenticationFailedCause()

    /**
     * Represents a case when authentication mechanism failed
     * @param message describing the cause of the authentication failure
     */
    public open class Error(public val message: String) : AuthenticationFailedCause() {
        @Suppress("UNUSED_PARAMETER")
        @Deprecated("Use message instead of cause.")
        public constructor(vararg placeholder: Unit, cause: String) : this(message = cause)

        /**
         * Contains error message explaining the reason of auth failure.
         */
        @Deprecated("Use message instead.", ReplaceWith("message"))
        public val cause: String get() = message
    }
}
