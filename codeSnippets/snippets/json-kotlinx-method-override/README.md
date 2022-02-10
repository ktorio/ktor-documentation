# XHttpMethodOverride
A sample project demonstrating how to use the `XHttpMethodOverride` plugin to tunnel HTTP methods inside another HTTP Header.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :json-kotlinx-method-override:run

# Windows
gradlew.bat :json-kotlinx-method-override:run
```

Then, open the [post.http](post.http) file and make a post request with the `X-Http-Method-Override` header set to `DELETE`. The server should return the `204 No Content` status code.
