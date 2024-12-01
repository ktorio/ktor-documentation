package core.extension

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.server.testing.ApplicationTestBuilder
import org.example.ktor.core.extension.configuredForApi

fun ApplicationTestBuilder.createConfiguredClient(
    additionalConfig: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit = {}
): HttpClient {
    return createClient {
        additionalConfig()
    }.configuredForApi()
}