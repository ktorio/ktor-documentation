import io.ktor.client.request.*
import io.ktor.http.*

client.post<Unit>("http://localhost:8080/post") {
    contentType(ContentType.Application.Json)
    body = Customer("Jet", "Brains")
}
