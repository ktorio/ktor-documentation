# Sessions - Server
A sample project demonstrating how to store session data on a server and pass a session ID in cookies.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :session-cookie-server:run

# Windows
.\gradlew.bat :session-cookie-server:run
```

* Go to the [http://0.0.0.0:8080/login](http://0.0.0.0:8080/login) page to create a session with a hard-coded user identifier.
* Open the [http://0.0.0.0:8080/logout](http://0.0.0.0:8080/logout) page to clear session data.