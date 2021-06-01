# SSL

A sample Ktor project with an [SSL](https://ktor.io/docs/ssl.html) connection.

This application is written as an embedded application with a custom `main` function 
[here](src/Main.kt) to generate temporary SSL certificate before starting a Netty engine.  

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :ssl:run
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.
You should get redirected to secure page at [https://localhost:8443/](https://localhost:8443/).  
A security dialog is shown because this sample uses a [self-signed temporary certificate](https://ktor.io/docs/self-signed-certificate.html).
  
## HTTPS Redirect

This sample also installs the [HTTPS redirect](https://ktor.io/docs/https-redirect.html) plugin to automatically redirect to HTTPS port:

```kotlin
install(HttpsRedirect) {
    sslPort = 8443
}
```
