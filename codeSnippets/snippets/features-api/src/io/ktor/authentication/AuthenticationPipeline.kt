// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.authentication

import io.ktor.application.*
import io.ktor.util.pipeline.*

/**
 * Represents authentication [Pipeline] for checking and requesting authentication
 */
public class AuthenticationPipeline : Pipeline<AuthenticationContext, ApplicationCall>(CheckAuthentication, RequestAuthentication) {

    public companion object {
        /**
         * Phase for checking if user is already authenticated before all mechanisms kicks in
         */
        public val CheckAuthentication: PipelinePhase = PipelinePhase("CheckAuthentication")

        /**
         * Phase for authentications mechanisms to plug into
         */
        public val RequestAuthentication: PipelinePhase = PipelinePhase("RequestAuthentication")
    }
}
