fun main() {
    embeddedServer(Netty, env = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            main()
        }

        connector {
            port = 8080
            host = "127.0.0.1"
        }
    }).start(true)
}