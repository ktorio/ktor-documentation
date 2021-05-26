# Form authentication

A sample project demonstrating how to use [form HTTP authentication](https://ktor.io/docs/form.html) in Ktor. In this sample, an HTML form is built using [HTML DSL](https://ktor.io/docs/html-dsl.html).

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-form:run

# Windows
gradlew.bat :auth-form:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) and enter the `jetbrains`/`foobar` credentials in a login page to see a greeting.