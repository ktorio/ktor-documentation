# Client handle non-2xx exceptions

A sample Ktor project showing how to customize default validation and handle exceptions for non-2xx responses in a specific way using [HttpCallValidator](https://ktor.io/docs/response-validation.html).

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
# macOS/Linux
./gradlew :client-customize-default-validation:run

# Windows
gradlew.bat :client-customize-default-validation:run
```

A client should raise a `MissingPageException` for a requested page.

