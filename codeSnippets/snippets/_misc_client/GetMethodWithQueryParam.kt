import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

val response: HttpResponse = client.get("http://localhost:8080/products") {
    parameter("price", "asc")
}