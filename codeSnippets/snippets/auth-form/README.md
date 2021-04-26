# Form authentication

A sample project demonstrating how to use [form HTTP authentication](https://ktor.io/docs/basic.html) in Ktor.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-form:run

# Windows
gradlew.bat :auth-form:run
```

Then, send form credentials in one of the following ways:
* Open the [post.http](post.http) file and make a post request with credentials using an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html).
* Execute the following command in a terminal:
   ```Bash
  curl -X POST --location "http://localhost:8080" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=jetbrains&password=foobar"
   ```
