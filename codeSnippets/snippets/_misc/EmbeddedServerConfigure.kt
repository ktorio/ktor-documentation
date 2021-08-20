fun main() {
    embeddedServer(Netty, port = 8080) {
        // ...
    }.start(wait = true)
}