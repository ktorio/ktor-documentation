# Task Manager Application with Kotlin Multiplatform

An example Task Manager application built with Ktor and Kotlin Multiplatform
by following the steps in the [Full-stack development with Kotlin Multiplatform
](https://ktor.io/docs/full-stack-development-with-kotlin-multiplatform.html) tutorial.

## Run the server

1. In a terminal window run the following command from the root directory to build an executable that includes both the
   code and all the dependencies it requires:

  ```shell
  ./gradlew :server:runFatJar
  ```

2. Use the following command to start the server:

```shell
./gradlew :server:run
```

3. Navigate to the following URLs:

- [http://0.0.0.0:8080/tasks](http://0.0.0.0:8080/tasks) to see a complete list of tasks in JSON format.
- [http://0.0.0.0:8080/tasks/byPriority/Medium](http://0.0.0.0:8080/tasks/byPriority/Medium) to see tasks filtered
  by `Medium` priority.

## Run the client

In Android Studio, select and run the following configurations:

- iOSApp to open the app on iOS
- composeApp to open the app on Android
- desktop to open the app on Desktop