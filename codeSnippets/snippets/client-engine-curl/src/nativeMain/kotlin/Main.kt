import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val client = HttpClient(Curl) {
        engine {
            sslVerify = false
        }
    }
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}
