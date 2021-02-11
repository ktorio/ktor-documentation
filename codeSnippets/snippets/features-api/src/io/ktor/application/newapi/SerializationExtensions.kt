// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.application.newapi

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.*

public fun KtorFeature.SendContext.serializeToString(callback: suspend SendExecution.(Any) -> String?): Unit =
    onSend { callback(context)?.let { proceedWith(it) } }

public fun KtorFeature.SendContext.serializeToBytes(callback: suspend SendExecution.(Any) -> ByteArray?): Unit =
    onSend { callback(context)?.let { proceedWith(it) } }

public fun KtorFeature.SendContext.serializeToChanel(callback: suspend SendExecution.(Any) -> ByteWriteChannel?): Unit =
    onSend { callback(context)?.let { proceedWith(it) } }


public fun <T : Any> KtorFeature<T>.serializeDataToString(callback: suspend SendExecution.(Any) -> String?): Unit =
    extendResponseHandling { serializeToString(callback) }

public fun <T : Any> KtorFeature<T>.serializeDataToBytes(callback: suspend SendExecution.(Any) -> ByteArray?): Unit =
    extendResponseHandling { serializeToBytes(callback) }

public fun <T : Any> KtorFeature<T>.serializeDataToChanel(callback: suspend SendExecution.(Any) -> ByteWriteChannel?): Unit =
    extendResponseHandling { serializeToChanel(callback) }
