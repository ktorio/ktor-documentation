# Task Manager Application with Kotlin Multiplatform

An example Task Manager application built with Ktor and Kotlin Multiplatform
by following the steps in the [Full-stack development with Kotlin Multiplatform
](https://ktor.io/docs/full-stack-development-with-kotlin-multiplatform.html) tutorial.

## Set the localhost IP address

Because it's not possible to make calls to `0.0.0.0` or `localhost` from code running on an Android Virtual Device or the
iPhone simulator, you need to specify a host address for the HTTP client.

1. Navigate to the `TaskApi.kt` file in `composeApp/src/commonMain/kotlin/org/example/ktor/network`.
2. In the `defaultRequest` function replace the value of the `host` property with the IP address of your machine.

## Run the server

Use the following command to start the server:

```shell
./gradlew :server:run
```

The server will then run at [http://0.0.0.0:8080/](). To test the endpoinds, navigate to the following URLs:

- [http://0.0.0.0:8080/tasks](http://0.0.0.0:8080/tasks) to see a complete list of tasks in JSON format.
- [http://0.0.0.0:8080/tasks/byPriority/Medium](http://0.0.0.0:8080/tasks/byPriority/Medium) to see tasks filtered
  by `Medium` priority.

## Run the client

In Fleet, press `Cmd + R` to open the run configurations popup window and choose from the following configurations:

- `iOSApp` to run the app on iOS
- `composeApp` to run the app on Android
- `composeApp [desktop]` to run the app on Desktop

## Test the application

To run all integration tests in the `integration-test` module, use the following command:

```shell
./gradlew :integration-test:test
```
