# Receiving requests

An application that receives request bodies in different formats.

>The application is used to demonstrate the content described in the [Receiving requests chapter](https://ktor.io/docs/migration-from-express-js.html#receive-request)
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

Use the [post.http](post.http) file to manually test the `HTTP` endpoints directly from the IDE.