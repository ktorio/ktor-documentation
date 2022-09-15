import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, configure = {
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
    }) {
        // ...
    }.start(wait = true)
}
