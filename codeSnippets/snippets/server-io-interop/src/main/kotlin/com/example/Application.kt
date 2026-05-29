package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import kotlinx.io.writeString
import java.io.ByteArrayOutputStream

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    routing {
        get("/sink") {
            call.respondBytesWriter {
                val sink: RawSink = this.asSink()
                sink.buffered().use { buffered ->
                    buffered.writeString("Hello from kotlinx-io Sink!")
                }
            }
        }

        get("/raw-sink") {
            val buffer = Buffer()
            val channel = buffer.asByteWriteChannel()
            channel.writeByte(42)
            channel.writeFully("Hello via RawSink".toByteArray())
            channel.flushAndClose()
            call.respondBytes(buffer.readByteArray())
        }

        get("/output-stream") {
            val out = ByteArrayOutputStream()
            val channel = out.asByteWriteChannel()
            channel.writeFully("Hello from OutputStream-backed channel!".toByteArray())
            channel.flushAndClose()
            call.respondBytes(out.toByteArray())
        }
    }
}
