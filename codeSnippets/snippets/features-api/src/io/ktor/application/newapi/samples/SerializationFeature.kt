package io.ktor.application.newapi.samples

import io.ktor.application.*
import io.ktor.application.newapi.*
import io.ktor.response.*
import io.ktor.routing.*

class Formula(val x: String, val execute: (String) -> String)

val FormulaSerialization = KtorFeature.makeFeature("F", {}) {
    serializeDataToString { f ->
        if (f !is Formula) return@serializeDataToString null
        f.execute(f.x)
    }
}

// Usage of FormulaSerialization feature
fun Application.formulaModule() {
    install(FormulaSerialization)

    routing {
        get("/formula") {
            // After FormulaSerialization was installed call.respond will work with Formula type:
            call.respond(Formula("cake") { x -> "x * x + 1".replace("x", x) })
        }
    }
}