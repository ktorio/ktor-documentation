import com.typesafe.config.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.*

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
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
