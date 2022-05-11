# Parallel requests

A sample Ktor project showing how to send several [requests](https://ktor.io/docs/request.html) asynchronously.

## Running
Before running this sample, start a server from the [simulate-slow-server](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/simulate-slow-server) example:
```bash
./gradlew :simulate-slow-server:run
```
In this example, a server adds a two-second delay to each response.

Then, run this sample by executing the following command in a repository's root directory:
```bash
./gradlew :client-parallel-requests:run
```
A server should respond with almost the same time values:
```
Response time: 14:42:58.737571
Response time: 14:42:58.737577
```
If you try to send sequential requests instead, the difference between responses will be at least two seconds.