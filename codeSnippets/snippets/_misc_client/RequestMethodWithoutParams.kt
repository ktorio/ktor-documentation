import io.ktor.client.request.*
import io.ktor.client.statement.*

val response: HttpResponse = client.request("https://ktor.io/") {
    // Configure request parameters exposed by HttpRequestBuilder
}