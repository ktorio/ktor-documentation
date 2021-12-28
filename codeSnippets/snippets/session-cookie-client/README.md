# Sessions - Client
A sample project demonstrating how to store session data in [cookies](https://ktor.io/docs/cookie-header.html).

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :session-cookie-client:run

# Windows
gradlew.bat :session-cookie-client:run
```

Then, open the [http://localhost:8080/user](http://localhost:8080/user) page to open the main page. It should display that a session doesn't exist or is expired. You can perform the following actions:
* Go to the [http://0.0.0.0:8080/login](http://0.0.0.0:8080/login) page to create a session with a hard-coded user identifier.
* Try to reload a page to see how a session counter is increasing.
* Open the [http://0.0.0.0:8080/logout](http://0.0.0.0:8080/logout) page to clear session data on a server.