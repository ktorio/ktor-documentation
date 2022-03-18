# Custom plugin

A sample Ktor project demonstrating how to create [custom plugins](https://ktor.io/docs/custom-plugins.html) in Ktor.

## Running

To run a sample, execute the following command in a repository's root directory:

```bash
./gradlew :custom-plugin:run
```

### SimplePlugin

[SimplePlugin](../custom-plugin/src/main/kotlin/com/example/plugins/SimplePlugin.kt) displays a greeting in the console output:

```Bash
2021-10-14 14:54:08.269 [main] INFO  Application - Autoreload is disabled because the development mode is off.
SimplePlugin is installed!
2021-10-14 14:54:08.900 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

### RequestLoggingPlugin

To see [RequestLoggingPlugin](../custom-plugin/src/main/kotlin/com/example/plugins/RequestLoggingPlugin.kt) in action, visit several pages, for example, [http://0.0.0.0:8080/](http://0.0.0.0:8080/) and [http://0.0.0.0:8080/index](http://0.0.0.0:8080/index). A console will show the requested URLs:

```Bash
Request URL: http://0.0.0.0:8080/
Request URL: http://0.0.0.0:8080/index
```

### CustomHeaderPlugin

[CustomHeaderPlugin](../custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderPlugin.kt) adds a custom header to each response. You can open a browser's network tools for [http://0.0.0.0:8080/](http://0.0.0.0:8080/) and make sure that a header is added:

```HTTP
HTTP/1.1 200 OK
X-Custom-Header: Hello, world!
```

### DataTransformationPlugin
[DataTransformationPlugin](../custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationPlugin.kt) transforms a received body to an integer value and adds `1` to it. Then, it adds `1` one more time when sending a response. To test a plugin, open [post.http](post.http) and make a request.

### DataTransformationBenchmarkPlugin
[DataTransformationBenchmarkPlugin](../custom-plugin/src/main/kotlin/com/example/plugins/DataTransformationBenchmarkPlugin.kt) uses attributes to calculate the time between receiving a request and reading a body. To see it in action, open [post.http](post.http) and make a request. Then, open a console to see a delay value:

```Bash
Request URL: http://localhost:8080/transform-data
Read body delay (ms): 52
```