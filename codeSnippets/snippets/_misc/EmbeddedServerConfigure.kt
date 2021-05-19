fun main() {
    embeddedServer(Netty, port = 8000) {
        // ...
    }.start(wait = true)
}