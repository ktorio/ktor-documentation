// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.authentication

import io.ktor.application.*
import io.ktor.util.*
import io.ktor.util.pipeline.*

/**
 * Represents authentication challenging procedure requested by authentication mechanism
 */
public class AuthenticationProcedureChallenge {
    internal val register = mutableListOf<
        Pair<AuthenticationFailedCause,
            PipelineInterceptor<AuthenticationProcedureChallenge, ApplicationCall>>>()

    /**
     * List of currently installed challenges except errors
     */
    public val challenges: List<PipelineInterceptor<AuthenticationProcedureChallenge, ApplicationCall>>
        get() = register.filter { it.first !is AuthenticationFailedCause.Error }.sortedBy {
            when (it.first) {
                AuthenticationFailedCause.InvalidCredentials -> 1
                AuthenticationFailedCause.NoCredentials -> 2
                else -> throw NoWhenBranchMatchedException("${it.first}")
            }
        }.map { it.second }

    /**
     * List of currently installed challenges for errors
     */
    @KtorExperimentalAPI
    public val errorChallenges: List<PipelineInterceptor<AuthenticationProcedureChallenge, ApplicationCall>>
        get() = register.filter { it.first is AuthenticationFailedCause.Error }.map { it.second }

    /**
     * Represents if a challenge was successfully sent to the client and challenging should be stopped
     */
    @Volatile
    public var completed: Boolean = false
        private set

    /**
     * Completes a challenging procedure
     */
    public fun complete() {
        completed = true
    }

    override fun toString(): String = "AuthenticationProcedureChallenge"
}
