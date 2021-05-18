import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.post("http://localhost:8080/post") {
    body = content
    onDownload { bytesSendTotal: Long, contentLength: Long -> TODO("update UI") }
}
