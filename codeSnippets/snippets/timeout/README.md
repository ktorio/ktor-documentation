# Timeout

A sample Ktor project demonstrating how to use the [HttpTimeout](https://ktor.io/docs/timeout.html) plugin.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :timeout:run
```

This example consists of two endpoints:
* `/timeout` emulates some long-running process that might hang up.
* `/proxy` represents a proxy to `/timeout` that protects a user against such hang-ups. 
  
If a user connects to `/proxy` and request hangs, proxy automatically aborts a request using `HttpTimeout` feature.