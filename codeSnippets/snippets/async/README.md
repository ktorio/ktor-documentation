# Async

A sample Ktor project demonstrating long-running asynchronous computation that happens in a separate thread-pool context.
For testing, we added `Random` and `DelayProvider` to the main module.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :async:run
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.  
