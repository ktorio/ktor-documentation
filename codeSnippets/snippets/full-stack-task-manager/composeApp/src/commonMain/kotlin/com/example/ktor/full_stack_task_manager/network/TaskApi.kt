import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class TaskApi(private val httpClient: HttpClient) {

    suspend fun getAllTasks(): List<Task> {
        return httpClient.get("tasks").body()
    }

    suspend fun removeTask(task: Task) {
        httpClient.delete("tasks/${task.name}")
    }

    suspend fun updateTask(task: Task) {
        httpClient.post("tasks") {
            contentType(ContentType.Application.Json)
            setBody(task)
        }
    }
}