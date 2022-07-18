[//]: # (title: Cookies)

<tldr>
<var name="example_name" value="client-cookies"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The HttpCookies plugin handles cookies automatically and keep them between calls in a storage.
</link-summary>

The Ktor client allows you to handle cookies manually in the following ways:
* The `cookie` function allows you to append a cookie to a [specific request](request.md#cookies).
* The `setCookie` function enables you to parse the `Set-Cookie` header value received in a [response](response.md#headers).

The [HttpCookies](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.cookies/-http-cookies/index.html) plugin handles cookies automatically and keep them between calls in a storage. By default, it uses an in-memory storage, but you can also implement a persistent storage using [CookiesStorage](#custom_storage).

## Add dependencies {id="add_dependencies"}
`HttpCookies` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.

## Install and configure HttpCookies {id="install_plugin"}

To install `HttpCookies`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
```kotlin
```
{src="snippets/client-cookies/src/main/kotlin/com/example/Application.kt" lines="12-14"}

This is enough to enable the Ktor client to keep cookies between requests. You can find the full example here: [client-cookies](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-cookies).


The `HttpCookies` plugin also allows you to add a specific set of cookies to each request by using `ConstantCookiesStorage`. This might be useful in a test case that verifies a server response. The example below shows how to add a specified cookie to all requests for a specific domain:

```kotlin
val client = HttpClient(CIO) {
    install(HttpCookies) {
        storage = ConstantCookiesStorage(Cookie(name = "user_name", value = "jetbrains", domain = "0.0.0.0"))
    }
}
```

## Get cookies {id="get_cookies"}

The client provides the `cookies` function to obtain all the cookies for the specified URL:

```kotlin
client.cookies("http://0.0.0.0:8080/")
```

## Custom cookie storage {id="custom_storage"}

If required, you can create a custom cookie storage by implementing the [CookiesStorage](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins.cookies/-cookies-storage/index.html) interface:

```kotlin
val client = HttpClient(CIO) {
    install(HttpCookies) {
        storage = CustomCookiesStorage()
    }
}

public class CustomCookiesStorage : CookiesStorage {
    // ...
}
```

You can use [AcceptAllCookiesStorage](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/plugins/cookies/AcceptAllCookiesStorage.kt) as a reference.
