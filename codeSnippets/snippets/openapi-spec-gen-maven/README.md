# Ktor OpenAPI Maven

This project demonstrates how to use the Ktor compiler plugin for inferring OpenAPI schemas from Kotlin code when
using Maven.

Because there is no maven bridge published for the Ktor compiler plugin yet, it requires manual configuration.

## Building & Running

To build or run the project, use one of the following tasks from the project's root directory:

| Task                                            | Description          |
|-------------------------------------------------|----------------------|
| `mvn test`                                      | Run the tests        |
| `mvn package`                                   | Build the project    |
| `java -jar target/ktor-openapi-maven-0.0.1.jar` | Run the server (JAR) |
| `mvn exec:java`                                 | Run the server (Dev) |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

Navigate to `http://localhost:8080/swagger` to see the Swagger UI documentation.

