# Client handle response exceptions

A sample Ktor project showing how to handle client exceptions using [HttpResponseValidator](https://ktor.io/docs/response-validation.html).

you want different handling of non 200 responses - you can use handleResponseException  with minor code change

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
# macOS/Linux
./gradlew :client-handle-response-exception:run

# Windows
gradlew.bat :client-handle-response-exception:run
```

A client should raise a `MissingPageException` for a requested page.

