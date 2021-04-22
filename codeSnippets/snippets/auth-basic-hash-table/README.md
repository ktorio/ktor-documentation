# Basic authentication with UserHashedTableAuth

A sample project demonstrating how to use [basic HTTP authentication](https://ktor.io/docs/basic.html) to authenticate users whose credentials are stored in an in-memory table.


when usernames and password hashes are stored in an in-memory table.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-basic-hash-table:run

# Windows
gradlew.bat :auth-basic-hash-table:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) in a web browser and enter one of the credentials specified in [hashedUserTable](src/main/kotlin/com/example/Application.kt).
