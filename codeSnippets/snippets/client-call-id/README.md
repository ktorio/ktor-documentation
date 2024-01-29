# Client call id

A sample Ktor project showing how to use the [CallId](https://ktor.io/docs/client-call-id.html) client plugin.
The project consists of two parts: an API service and a client.
The API service utilizes the `callId` plugin on the server and configures it to retrieve a call ID from the request
header or use a generated call ID.

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

1. To start the API service, navigate to `CallIdService.kt` and click on the play button next to the `main()` function.

2. Run the client sample by executing the following command:

```bash
./gradlew :client-call-id:run
```

The client makes 3 requests to the API and prints out information about the call ID.

The expected output is:
```
Handling request `/call` from client with call.callId: call-id-client and X-RequestId: call-id-client
Handling request `/call` from client with call.callId: call-id-client and X-RequestId: call-id-client
Handling request `/call` from client with call.callId: call-id-client-2 and X-RequestId: call-id-client-2
```