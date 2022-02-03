import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val client = HttpClient(Darwin) {
        engine {
            configureRequest {
                setAllowsCellularAccess(true)
            }
        }
    }
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}
