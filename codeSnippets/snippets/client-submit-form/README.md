# Client submit form

A sample Ktor project showing how to send [form parameters](https://ktor.io/docs/request.html#form_parameters) encoded using `multipart/form-data`.

## Running

First, you need to run a [server sample](../post-form-parameters). Execute the following command in a repository's root folder:

```bash
./gradlew :post-form-parameters:run
```

Then, execute the following command to send form parameters:

```bash
./gradlew :client-submit-form:run
```