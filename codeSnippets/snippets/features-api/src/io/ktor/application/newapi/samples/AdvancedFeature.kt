package io.ktor.application.newapi.samples

import io.ktor.application.newapi.KtorFeature.Companion.makeFeature
import io.ktor.request.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.set

class CallCounter {
    val logger = Logger.getLogger("Calls")
    private val callCount = mutableMapOf<String, Int>() // url -> count of requests

    // increments counter for this url
    internal fun countCall(url: String) {
        callCount[url] = callCount.getOrPut(url) { 0 } + 1
    }

    // logs successful call with counter
    internal fun logSuccess(url: String) {
        logger.log(Level.INFO, "Successfully processed call for url=$url : ${callCount[url] ?: 0}")
    }

    // logs unsuccessful call with counter
    internal fun logUnhandled(url: String) {
        logger.log(Level.INFO, "Unhandled call for url=$url : ${callCount[url] ?: 0}")
    }
}

val CallCounterFeature = makeFeature(
    name = "CallCounter",
    createConfiguration = { CallCounter() }
) {
    extendCallHandling {
        monitoring { call ->
            // increment counter before performing the call and executing any other feature
            feature.countCall(call.request.uri)
        }
        onCall { call ->
            // log the fact that call was successful and number of similar requests
            feature.logSuccess(call.request.uri)
        }
        fallback {
            // log that call was not successful and number of similar requests
            feature.logUnhandled(call.request.uri)
        }
    }
}