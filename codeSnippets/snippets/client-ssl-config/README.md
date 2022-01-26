# Client SSL config

A sample Ktor project showing how to configure SSL settings for different [engines](https://ktor.io/docs/http-client-engines.html).

## Running

Before running this sample, start a server from the [ssl-engine-main](../ssl-engine-main) example:
```bash
./gradlew :ssl-engine-main:run
```

In this example, the Ktor server serves SSL directly using a self-signed certificate.

Then, run this sample to make a request and get a response.

```bash
./gradlew :client-ssl-config:run
```
