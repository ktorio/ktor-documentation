# Basic authentication with UserHashedTableAuth

A sample project demonstrating how to use [basic HTTP authentication](https://ktor.io/docs/basic.html) when user names and password hashes are stored in an in-memory table.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :auth-basic-hash-table:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) and enter one of the credentials specified in  [hashedUserTable](src/main/kotlin/com/example/Application.kt).
