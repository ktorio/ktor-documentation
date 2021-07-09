# Defining configuration properties in tests

A sample Ktor project demonstrating how to [define configuration properties in tests](https://ktor.io/docs/testing.html#configuration-properties). 

In this project, the `upload.dir` custom property defined in the [application.conf](src/main/resources/application.conf) specifies a directory used to upload a file to a server. In [tests](src/test/kotlin/), you can see two approaches of defining this property:
* By using the `MapApplicationConfig.put` function.
* By loading a property value from `application.conf`.

