import io.ktor.client.*
import io.ktor.client.engine.winhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val client = HttpClient(WinHttp) {
        engine {
            protocolVersion = HttpProtocolVersion.HTTP_1_1
        }
    }
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}
