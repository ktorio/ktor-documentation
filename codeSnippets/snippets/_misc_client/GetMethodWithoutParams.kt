import io.ktor.client.request.*
import io.ktor.client.statement.*

val response: HttpResponse = client.get("https://ktor.io/")