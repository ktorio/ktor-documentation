# Session authentication
A sample project demonstrating how to authenticate users via [sessions](https://ktor.io/docs/sessions.html). 

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-session:run

# Windows
gradlew.bat :auth-session:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) in a web browser and click **Login** to authenticate with a hard-coded session ID [specified](https://ktor.io/docs/sessions.html#set-content) in the `login` route. After authentication, you can click **Logout** to clear a session and return to the main page.