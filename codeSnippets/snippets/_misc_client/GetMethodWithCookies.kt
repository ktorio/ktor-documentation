import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.date.*

val response: HttpResponse = client.get("https://ktor.io/") {
    cookie(name = "user_name", value = "jetbrains", expires = GMTDate(
        seconds = 0,
        minutes = 0,
        hours = 10,
        dayOfMonth = 1,
        month = Month.APRIL,
        year = 2022
    ))
}