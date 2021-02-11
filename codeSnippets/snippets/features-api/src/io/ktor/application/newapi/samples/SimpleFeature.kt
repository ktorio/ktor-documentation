package io.ktor.application.newapi.samples

import io.ktor.application.*
import io.ktor.application.newapi.KtorFeature.Companion.makeFeature
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

class MyConfig {
    // If a header with name inputHeader is set in the query,
    // some data should be in response header
    var inputHeader: String = "MyFeature"

    // Defines mapping between input value in request header
    // and output value that should be sent in response header
    var processResponse: (String) -> String = { "" }
}

// create feature with name "SimpleFeature" and config MyConfig
val MyFeature = makeFeature(
    name = "SimpleFeature",
    createConfiguration = { MyConfig() }
) {
    var inputValue: String? = null

    // `feature` field is accessible inside `makeFeatue` block.
    // It represents instance of configuration of the type MyConfig.
    // It allows using values of all configuration properties defined in install block.
    val headerName = feature.inputHeader
    val process = feature.processResponse

    // onReceive callback defines what happens when server receives a request inside any call
    onReceive { call ->
        inputValue = call.request.header(headerName)
    }

    // onRespond callback defines what happens when server sends a response for any call
    onRespond { call ->
        if (inputValue != null) {
            val outputValue = process(inputValue!!)
            call.response.header(headerName, outputValue)
        }
    }
}


fun Application.usageExample() {
    val DOLLAR_TO_EUR = 0.83 // currency rate

    install(MyFeature) {
        inputHeader = "money" // in $
        processResponse = { dollars -> (dollars.toDouble() * DOLLAR_TO_EUR).toString() } // in EUR
    }


    routing {
        get("/money") {
            // Here the `inputValue` (amount in $) of our feature will be received from a header:
            val s = call.receiveText()

            // And now we'll respond with `inputHeader` set to new value (amount in EUR):
            call.respond("You said \"$s\"? Now take a look at your `money` header!!!")
        }
    }
}

fun Application.simpleModule() {
    install(MyFeature) {
        inputHeader = "Name"
        processResponse = { it + "(processed)" }
    }
}