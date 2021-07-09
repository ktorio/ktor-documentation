# Gson

A sample Ktor project demonstrating the [ContentNegotiation](https://ktor.io/docs/serialization.html) plugin using Gson.

## Running

To run this sample, execute the following command in a repository's root directory:

```
./gradlew :gson:run
```
 
Use the following command scripts for testing:

```bash
curl -v --compressed --header "Accept: application/json" http://localhost:8080/v1
```

A server should respond with something like this:

```json
{
  "name": "root",
  "items": [
    {
      "key": "A",
      "value": "Apache"
    },
    {
      "key": "B",
      "value": "Bing"
    }
  ],
  "date": {
    "year": 2018,
    "month": 3,
    "day": 2
  }
}
```

The result is pretty printed, to show off how to configure Gson, but it is possible to use the default gson as well

Another test:

```bash
curl -v --compress --header "Accept: application/json" http://localhost:8080/v1/item/A
```
 
Response:

```json
{
  "key": "A",
  "value": "Apache"
}
```
        
## Using HTTP client

You can make requests to this sample server using Ktor HTTP client. 
See [client-json](../client-json/README.md) sample.