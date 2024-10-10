# Sending responses

An application that shows how to access route and query parameters.

>The application is used to demonstrate the content described in the [Sending responses chapter](https://ktor.io/docs/migration-from-express-js.html#send-json)
>from the "Migrating from Express to Ktor" article.

## Build

To build the application, run the following command from the project's root directory:

```shell
./gradlew build
```

## Run

To run the application, run the following command from the project's root directory:

```shell
./gradlew run
```
The application will be running at [http://0.0.0.0:8080](http://0.0.0.0:8080)

## Test

Test the application by navigating to the following URLs:

- [http://0.0.0.0:8080/old](http://0.0.0.0:8080/old) redirects to `/moved`.
- [http://0.0.0.0:8080/json](http://0.0.0.0:8080/json) returns data displayed in a JSON format.
- [http://0.0.0.0:8080/file](http://0.0.0.0:8080/file) returns a file.
- [http://0.0.0.0:8080/file-attachment](http://0.0.0.0:8080/file-attachment) downloads a file.
