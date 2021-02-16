[//]: # (title: Testing)

<include src="lib.md" include-id="outdated_warning"/>

Ktor is designed to allow the creation of applications that are easily testable. And of course,
Ktor infrastructure itself is well tested with unit, integration, and stress tests.
In this section, you will learn how to test your applications.





## TestEngine

Ktor has a special kind engine `TestEngine`, that doesn't create a web server, doesn't bind to sockets and doesn't do
any real HTTP requests. Instead, it hooks directly into internal mechanisms and processes `ApplicationCall` directly.
This allows for fast test execution at the expense of maybe missing some HTTP processing details.
It's perfectly capable of testing application logic, but be sure to set up integration tests as well.

A quick walkthrough:

* Add `ktor-server-test-host` dependency to the `test` scope
* Create a JUnit test class and a test function
* Use `withTestApplication` function to set up a test environment for your Application
* Use the `handleRequest` function to send requests to your application and verify the results

## Building post/put bodies

### `application/x-www-form-urlencoded`

When building the request, you have to add a `Content-Type` header:

```kotlin
addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
```

And then set the `bodyChannel`, for example, by calling the `setBody` method:

```kotlin
setBody("name1=value1&name2=value%202")
```

Ktor provides an extension method to build a form urlencoded out of a `List` of key/value pairs:

```kotlin
fun List<Pair<String, String>>.formUrlEncode(): String
```

So a complete example to build a post request urlencoded could be:

```kotlin
val call = handleRequest(HttpMethod.Post, "/route") {
   addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
   setBody(listOf("name1" to "value1", "name2" to "value2").formUrlEncode())
}
```

### `multipart/form-data`

When uploading big files, it is common to use the multipart encoding, which allows sending
complete files without preprocessing. Ktor's test host provides a `setBody` extension method
to build this kind of payload. For example:

```kotlin
val call = handleRequest(HttpMethod.Post, "/upload") {
    val boundary = "***bbb***"

    addHeader(HttpHeaders.ContentType, ContentType.MultiPart.FormData.withParameter("boundary", boundary).toString())
    setBody(boundary, listOf(
        PartData.FormItem("title123", { }, headersOf(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Inline
                .withParameter(ContentDisposition.Parameters.Name, "title")
                .toString()
        )),
        PartData.FileItem({ byteArrayOf(1, 2, 3).inputStream().asInput() }, {}, headersOf(
            HttpHeaders.ContentDisposition,
            ContentDisposition.File
                .withParameter(ContentDisposition.Parameters.Name, "file")
                .withParameter(ContentDisposition.Parameters.FileName, "file.txt")
                .toString()
        ))
    ))
}
```

## Defining configuration properties in tests

In tests, instead of using an `application.conf` to define configuration properties,
you can use the `MapApplicationConfig.put` method:

```kotlin
withTestApplication({
    (environment.config as MapApplicationConfig).apply {
        // Set here the properties
        put("youkube.session.cookie.key", "03e156f6058a13813816065")
        put("youkube.upload.dir", tempPath.absolutePath)
    }
    main() // Call here your application's module
})
```

## HttpsRedirect feature

The HttpsRedirect changes how testing is performed.
Check the [testing section of the HttpsRedirect feature](https-redirect.md) for more information.

## Testing several requests preserving sessions/cookies
{id="preserving-cookies"}

You can easily test several requests in a row keeping the `Cookie` information among them. By using the `cookiesSession` method.
This method defines a session context that will hold cookies, and exposes a `CookieTrackerTestApplicationEngine.handleRequest`
extension method to perform requests in that context.

For example:

```kotlin
@Test
fun testLoginSuccessWithTracker() = testApp {
    val password = "mylongpassword"
    val passwordHash = hash(password)
    every { dao.user("test1", passwordHash) } returns User("test1", "test1@test.com", "test1", passwordHash)

    cookiesSession {
        handleRequest(HttpMethod.Post, "/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("userId" to "test1", "password" to password).formUrlEncode())
        }.apply {
            assertEquals(302, response.status()?.value)
            assertEquals("http://localhost/user/test1", response.headers["Location"])
            assertEquals(null, response.content)
        }

        handleRequest(HttpMethod.Get, "/").apply {
            assertTrue { response.content!!.contains("sign out") }
        }
    }
}
```

Note: `cookiesSession` is not included in Ktor itself, but you can add this boilerplate to use it:

```kotlin
fun TestApplicationEngine.cookiesSession(
    initialCookies: List<Cookie> = listOf(),
    callback: CookieTrackerTestApplicationEngine.() -> Unit
) {
    callback(CookieTrackerTestApplicationEngine(this, initialCookies))
}

class CookieTrackerTestApplicationEngine(
    val engine: TestApplicationEngine,
    var trackedCookies: List<Cookie> = listOf()
)

fun CookieTrackerTestApplicationEngine.handleRequest(
    method: HttpMethod,
    uri: String,
    setup: TestApplicationRequest.() -> Unit = {}
): TestApplicationCall {
    return engine.handleRequest(method, uri) {
        val cookieValue = trackedCookies.map { (it.name).encodeURLParameter() + "=" + (it.value).encodeURLParameter() }.joinToString("; ")
        addHeader("Cookie", cookieValue)
        setup()
    }.apply {
        trackedCookies = response.headers.values("Set-Cookie").map { parseServerSetCookieHeader(it) }
    }
}
```

## Example with dependencies

See full example of application testing in [ktor-samples-testable](https://github.com/ktorio/ktor-samples/tree/1.3.0/feature/testable).
Also, most [`ktor-samples`](https://github.com/ktorio/ktor-samples) modules provide
examples of how to test specific functionalities.

In some cases we will need some services and dependencies. Instead of storing them globally, we suggest you
to create a separate function receiving the service dependencies. This allows you to pass different
(potentially mocked) dependencies in your tests:

<tabs>

```groovy
// ...
dependencies {
    // ...
    testCompile("io.ktor:ktor-server-test-host:$ktor_version")
}
```



```kotlin
fun Application.testableModule() {
    testableModuleWithDependencies(
        random = SecureRandom()
    )
}

fun Application.testableModuleWithDependencies(random: Random) {
    routing {
        get("/") {
            call.respondText("Random: ${random.nextInt(100)}")
        }
    }
}
```



```kotlin
class ApplicationTest {
    class ConstantRandom(val value: Int) : Random() {
        override fun next(bits: Int): Int = value
    }

    @Test fun testRequest() = withTestApplication({
        testableModuleWithDependencies(
            random = ConstantRandom(7)
        )
    }) {
        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Random: 7", response.content)
        }
        with(handleRequest(HttpMethod.Get, "/index.html")) {
            assertFalse(requestHandled)
        }
    }
}
```

</tabs>