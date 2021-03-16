import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*

val response: HttpResponse = client.post("http://127.0.0.1:8080/") {
    body = TextContent(
        text = "Body content",
        contentType = ContentType.Text.Plain
    )
}