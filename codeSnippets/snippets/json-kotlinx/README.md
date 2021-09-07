# JSON - kotlinx.serialization
A sample project demonstrating how to use the `kotlinx.serialization` library to [serialize and deserialize JSON](https://ktor.io/docs/serialization.html) in Ktor.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :json-kotlinx:run

# Windows
gradlew.bat :json-kotlinx:run
```

Then, you can perform one of the following actions:
* Open [http://localhost:8080/customer/1](http://localhost:8080/customer/1) to make sure Ktor responds with a JSON object.
* Open the [post.http](post.http) file and make a post request to send a JSON object to a server. Ktor deserializes this object to a `Customer` object and saves it to a storage.
