package dataconversion

import io.ktor.server.application.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(DataConversion) {
        convert<LocalDate> { // this: DelegatingConversionService
            val formatter = DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4, 4, SignStyle.NEVER)
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .toFormatter(Locale.ROOT)

            decode { values -> // converter: (values: List<String>) -> Any?
                LocalDate.from(formatter.parse(values.single()))
            }

            encode { value -> // converter: (value: Any?) -> List<String>
                listOf(SimpleDateFormat.getInstance().format(value))
            }
        }
    }

    routing {
        get("/date") {
            val encodedDate = application.conversionService.toValues(call.parameters["date"])
            val decodedDate = application.conversionService.fromValues(encodedDate, typeInfo<LocalDate>())
            call.respondText("The date is $decodedDate")
        }
    }
}
