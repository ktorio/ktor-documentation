[//]: # (title: Utilities)
[//]: # (caption: Utilities exposed by Ktor (URL-Encoding))
[//]: # (category: advanced)
[//]: # (permalink: /advanced/utilities.html)
[//]: # (keywords: >-)
[//]: # (utilities formUrlEncode url-encoded application/x-www-form-urlencoded map to URL encoded string list to url encoded string Parameters parametersOf ApplicationCall.respondUrlEncoded: utilities formUrlEncode url-encoded application/x-www-form-urlencoded map to URL encoded string list to url encoded string Parameters parametersOf ApplicationCall.respondUrlEncoded)
[//]: # (ktor_version_review: 1.0.0)

## Handling URL-encoded properties
{id="url-encoded"}

Ktor exposes a few extension methods for parsing and generating url-encoded strings (in the `application/x-www-form-urlencoded` mimetype format).

URL-encoded strings look like: `param=value&other=hi`.

### Parsing:
{id="url-encoded-parse"}

There is an extension method for `String` that allows you to get a parsed `Parameters` object from it. You can limit the maximum number of parsed parameters with the optional `limit` parameter.

```kotlin
fun String.parseUrlEncodedParameters(defaultEncoding: Charset = Charsets.UTF_8, limit: Int = 1000): Parameters
```

### Encoding:
{id="url-encoded-encode"}

You can generate a URL-encoded string either from a List of Pairs of Strings or a `Parameters` instance: 

```kotlin
fun List<Pair<String, String?>>.formUrlEncode(): String
fun List<Pair<String, String?>>.formUrlEncodeTo(out: Appendable)
fun Parameters.formUrlEncode(): String
fun Parameters.formUrlEncodeTo(out: Appendable)
```

You can construct a URL-encoded string from `List<Pair<String, String>>` like this:

```kotlin
listOf(
	"error" to "invalid_request",
	"error_description" to "client_id is missing"
).formUrlEncode()
```

You can also construct it from a `Parameters` instance that you can instantiate by using the `Parameters.build` builder and then calling the `formUrlEncode` extension, or with the `parametersOf()` builder method:

```kotlin
Parameters.build {
	append("error", "invalid_request")
	append("error_description", "client_id is missing")
}.formUrlEncode()
```

The `parametersOf` builder has several signatures, and there is `Parameters` operator overloading (`+`) to join two `Parameters` instances:

```kotlin
parametersOf("a" to "b1", "a" to "b2").formUrlEncode()
(parametersOf("a", "b1") + parametersOf("a", "b2")).formUrlEncode()
parametersOf("a", listOf("b1", "b2")).formUrlEncode()
```

From a normal `Map<String, String>` you can also construct a URL-encoded string, but first you will have to create a List from it:

```kotlin
mapOf(
	"error" to "invalid_request",
	"error_description" to "client_id is missing"
).toList().formUrlEncode()
```

URL-encoded strings allow you to have repeated keys. If you use a `Map<String, String>` as base, you won't be able to represent repeated keys.
In this case consider using `parametersOf`, `List` or `Parameters.build`.
{ .note}

You can also construct it from a `Map<String, List<String>>` by *flatMapping* it first:

```kotlin
mapOf(
    "error" to listOf("invalid_request"),
    "error_descriptions" to listOf("client_id is missing", "server error")
).flatMap { map -> map.value.map { map.key to it } }.formUrlEncode()
```

In case you want to avoid constructing a whole String with the content and instead want to write to somewhere directly, you can use the `formUrlEncodeTo` method.

#### Responding URL-encoded

You can do something like:

```kotlin
call.respondUrlEncoded("hello" to listOf("world"))
```

by adding the following extension methods to your project:

```kotlin
suspend fun ApplicationCall.respondUrlEncoded(vararg keys: Pair<String, List<String>>) =
    respondUrlEncoded(parametersOf(*keys))

suspend fun ApplicationCall.respondUrlEncoded(parameters: Parameters) =
    respondTextWriter(ContentType.Application.FormUrlEncoded) {
        parameters.formUrlEncodeTo(this)
    }
```