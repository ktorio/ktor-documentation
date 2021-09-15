import e2e.WithTestServer
import e2e.defaultServer
import e2e.readString
import e2e.runGradleAppWaiting
import io.ktor.server.application.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest: WithTestServer() {
    override val server = defaultServer {
        routing {
            post("/upload") {
                val data = call.receiveMultipart()
                val descriptionPart = data.readPart() as PartData.FormItem
                val filePart = data.readPart() as PartData.FileItem

                call.respondText {
                    "${descriptionPart.name}:${descriptionPart.value}," +
                        "${filePart.name}:[${filePart.contentType}][${filePart.contentDisposition}]"
                }
            }
        }
    }

    @Test
    fun clientSendsMultipartData() {
        val output = runGradleAppWaiting().inputStream.readString()
        assertEquals(
            output,
            "description:Ktor logo,image:[image/png][form-data; name=image; filename=ktor_logo.png]\n"
        )
    }
}