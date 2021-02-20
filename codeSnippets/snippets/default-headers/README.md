# DefaultHeaders

A sample Ktor project showing the [DefaultHeaders](https://ktor.io/docs/default-headers.html) feature:
* adds the standard `Server` and `Date` headers
* overrides the `Server` header with a custom value
* adds a custom header with the `Custom-Header` name

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :default-headers:run
```

Then, open the [http://localhost:8080/](http://localhost:8080/) page to see headers added into each response.
