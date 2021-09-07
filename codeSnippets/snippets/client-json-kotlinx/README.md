# JSON Client - kotlinx.serialization

A sample project demonstrating how to use the `kotlinx.serialization` library to [serialize and deserialize JSON](https://ktor.io/docs/json.html) in a Ktor client.

## Running

Before running this sample, start the [json-kotlinx](../json-kotlinx) server sample:

```bash
# macOS/Linux
./gradlew :json-kotlinx:run

# Windows
gradlew.bat :json-kotlinx:run
```

Then, run this client sample to post and get JSON data:

```bash
# macOS/Linux
./gradlew :client-json-kotlinx:run

# Windows
gradlew.bat :client-json-kotlinx:run
```
