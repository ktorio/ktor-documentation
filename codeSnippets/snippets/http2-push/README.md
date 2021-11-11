# HTTP/2 Push

A sample Ktor project demonstrating server-side push for the [HTTP/2](https://ktor.io/docs/advanced-http2.html) protocol.
This application is written as an embedded application with a custom `main` function [here](src/main/kotlin/com/example/Main.kt) to generate a temporary SSL certificate before starting a Netty engine, because browsers support HTTP/2 only with SSL. 

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :http2-push:run
```
 
Then, navigate to [https://localhost:8443/](https://localhost:8443/) to see the sample home page.  
