import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.get("http://localhost:8080/products") {
    downloadProgress { bytesSendTotal: Long, contentLength: Long -> TODO("update UI") }
}