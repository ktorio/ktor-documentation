# Form authentication

A sample project demonstrating how to use [session authentication](https://ktor.io/docs/session-auth.html) for a user logged in through a [web form](https://ktor.io/docs/form.html).

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-form-session:run

# Windows
gradlew.bat :auth-form-session:run
```

Then, perform the following steps:
* Open [http://localhost:8080/login](http://localhost:8080/login) and enter the `jetbrains`/`foobar` credentials in a login page to see a greeting and visit count.
* Reload a page to see how a visit counter is increasing.
* Open the [http://0.0.0.0:8080/logout](http://0.0.0.0:8080/logout) page to clear session data on a server and return to the login page.
