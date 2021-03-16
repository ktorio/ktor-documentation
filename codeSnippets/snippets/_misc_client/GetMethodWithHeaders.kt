import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.get("https://ktor.io/") {
    headers {
        append(HttpHeaders.Accept, "text/html")
        append(HttpHeaders.Authorization, "token")
        append(HttpHeaders.UserAgent, "ktor client")
    }
}