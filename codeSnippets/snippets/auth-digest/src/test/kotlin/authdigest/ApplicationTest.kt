package authdigest

import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testAuthRoute() = testApplication {
        val client = createClient {
            install(Auth) {
                digest {
                    credentials {
                        DigestAuthCredentials(username = "jetbrains", password = "foobar")
                    }
                    realm = "Access to the '/' path"
                }
            }
        }
        client.get("/").let { response ->
            assertEquals("Hello, jetbrains!", response.bodyAsText())
        }
    }
}
