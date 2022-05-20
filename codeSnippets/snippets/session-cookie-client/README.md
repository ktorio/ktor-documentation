# Sessions - Client
A sample project demonstrating how to store session data in cookies.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :session-cookie-client:run
```

Then, open the [http://localhost:8080/user](http://localhost:8080/user) page. It should display that a session doesn't exist or is expired. You can perform the following actions:
* Go to the [http://localhost:8080/login](http://localhost:8080/login) page to create a session with a hard-coded session identifier.
* Try to reload a page to see how a session counter is increasing.
* Open the [http://localhost:8080/logout](http://localhost:8080/logout) page to clear session data.