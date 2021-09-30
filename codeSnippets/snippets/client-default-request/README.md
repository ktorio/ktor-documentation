# Client default request

A sample Ktor project showing how to use the [DefaultRequest](https://ktor.io/docs/default-request.html) plugin to provide default settings used for each request.

## Running

Before running this sample, start a server from the [ssl-embedded-server](../ssl-embedded-server) example:
```bash
./gradlew :ssl-embedded-server:run
```

In this example, the Ktor server serves SSL directly using a self-signed certificate.

Then, run this sample to make a request and get a response.

```bash
./gradlew :client-default-request:run
```

> Note that this example uses the CIO engine configured to accept all certificates. This approach should be used for development purposes only.
