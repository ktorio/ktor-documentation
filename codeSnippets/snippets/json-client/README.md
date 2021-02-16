# Json client

A sample Ktor project demonstrating the HTTP client with [JsonFeature ](https://ktor.io/docs/json-feature.html). 

## Running

First off, run one of the server-side samples:
* [gson](../gson/README.md)
* [jackson](../jackson/README.md)

Then, run this project with by executing the following command in a repository's root directory:

```
./gradlew :json-client:run
```

The resulting output should be:

```text
Requesting model...
Fetching items for 'root'...
Received: Item(key=A, value=Apache)
Received: Item(key=B, value=Bing)
```