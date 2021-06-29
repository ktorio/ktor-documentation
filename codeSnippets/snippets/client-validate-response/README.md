# Client validate 2xx responses

A sample Ktor project showing how to add custom validation of 2xx responses by using [HttpCallValidator](https://ktor.io/docs/response-validation.html). In this example, a client raises a `CustomResponseException` when error details come in a 2xx response in a JSON format.

## Running a server

Before running this sample, you need to run a server that sends a sample error as a JSON object. You can modify the [gson](../gson/README.md) sample to do this:
1. Declare a `Error` data class:
   ```kotlin
   data class Error(val code: Int, val message: String)
   ```
1. Create a error object:
   ```kotlin
   val error = Error(3, "Some custom error")
   ```
1. Send a error in a response:
   ```kotlin
   get("/v1") {
       call.respond(error)
   }
   ```
   
Now you can run a server:
```
./gradlew :gson:run
```


## Running a client

To run a client sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-validate-response:run
```
