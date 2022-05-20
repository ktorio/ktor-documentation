# Sessions - Server
A sample project demonstrating how to store session data on a server and pass a session ID in cookies.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :session-cookie-server:run
```

* Go to the [http://localhost:8080/login](http://localhost:8080/login) page to create a session with a hard-coded user identifier.
* Open the [http://localhost:8080/logout](http://localhost:8080/logout) page to clear session data.