[//]: # (title: Content Negotiation)
[//]: # (caption: Content conversion based on Content-Type and Accept headers)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/content-negotiation.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.ContentNegotiation)
[//]: # (children: /servers/features/content-negotiation/)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/content-negotiation.html: - /features/content-negotiation.html)
[//]: # (ktor_version_review: 1.0.0)

This feature provides automatic content conversion according to `Content-Type` and `Accept` headers.

{% include feature.html %}

## Basic Usage
{id="basic"}

The ContentNegotiation feature allows you to register and configure custom converters.

```kotlin
install(ContentNegotiation) {
    register(MyContentType, MyContentTypeConverter()) {
        // Optionally configure the converter...
    }
}
```

For example:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, JacksonConverter())
}
```

## Sending
{id="sending "}

When you respond with an object that is not directly handled, like a custom data class,
this feature checks the client's `Accept` header to determine which `Content-Type` will be 
used and thus which `ContentConverter` will be called.

```kotlin
call.respond(MyDataClass("hello", "world"))
```

Right now, the only supported ContentNegotiation strategy when sending, is the
client's `Accept` header. There is an [issue to implement other strategies](https://github.com/ktorio/ktor/issues/357).
{ .note} 

## Receiving
{id="receiving "}

When receiving, the `Content-Type` of the request will be used to determine
which `ContentConverter` will be used to process that request:

```kotlin
val myDataClass = call.receive<MyDataClass>()
```

## The ContentConverter interface
{id="content-converter"}

If you want to write your own converter, you have to implement the `ContentConverter` interface:

```kotlin
interface ContentConverter {
    suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any?
    suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any?
}
```

For example, the `GsonConverter` implementation looks like:

```kotlin
class GsonConverter(private val gson: Gson = Gson()) : ContentConverter {
    override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any? {
        return TextContent(gson.toJson(value), contentType.withCharset(context.call.suitableCharset()))
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val reader = channel.readRemaining().readText((context.call.request.contentCharset() ?: Charsets.UTF_8).newDecoder()).reader()
        return gson.fromJson(reader, request.type.javaObjectType)
    }
}
```

## Available out of the box ContentConverter
{id="available-converters"}

Ktor provide some content converters out of the box:

* `application/json`
    * [GsonConverter](/servers/features/content-negotiation/gson.html)
    * [JacksonConverter](/servers/features/content-negotiation/jackson.html)
    * [SerializationConverter](/servers/features/content-negotiation/serialization-converter.html)