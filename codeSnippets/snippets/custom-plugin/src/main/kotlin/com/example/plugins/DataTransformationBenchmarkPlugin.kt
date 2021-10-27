package com.example.plugins

import io.ktor.server.application.plugins.api.*
import io.ktor.util.*

val DataTransformationBenchmarkPlugin = createApplicationPlugin(name = "DataTransformationBenchmarkPlugin") {
    val benchmarkKey = AttributeKey<Long>("BenchmarkKey")
    onCall { call ->
        val startTime = System.currentTimeMillis()
        call.attributes.put(benchmarkKey, startTime)
    }

    onCallRespond { call ->
        val startTime = call.attributes[benchmarkKey]
        val endTime = System.currentTimeMillis()
        val diff = endTime - startTime
        println(diff)
    }
}