import io.ktor.client.request.*
import io.ktor.client.statement.*

val response: HttpResponse = client.request("https://ktor.io/") {
    // Configure request parameters exposed by [[[HttpRequestBuilder|https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/index.html]]]
}