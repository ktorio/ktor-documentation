[//]: # (title: DoubleReceive)
[//]: # (caption: DoubleReceive for request body)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/double-receive.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.DoubleReceive)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/double-receive.html: - /features/double-receive.html)
[//]: # (keywords: receive)
[//]: # (ktor_version_review: 1.2.3)

`DoubleReceive` feature provides the ability to invoke `ApplicationCall.receive` several times with no `RequestAlreadyConsumedException` exception. This usually makes sense when a feature is consuming a request body
so a handler is unable to receive it again.

This feature is experimental including all options and behaviour. It is not guaranteed to work the same way in future releases.
{ .note.experimental}





## Usage

Install `DoubleReceive` feature into the ApplicationCall

```kotlin
install(DoubleReceive)
```

After that you can receive from a call multiple times and every invocation may return the same instance.

```kotlin
val first = call.receiveText()
val theSame = call.receiveText()
```

Not every content could be received twice. For example, a stream or a channel can't be received twice unless `receiveEntireContent` option is enabled.
{ .note.warning}

Types that could be always received twice with this feature are: `ByteArray`, `String` and `Parameters` and all types provided by [ContentNegotiation](/servers/features/content-negotiation.html) feature (for example, objects deserialized from JSON payloads).

Receiving different types from the same call is not guaranteed to work without `receiveEntireContent` but may work in some specific cases. For example, receiving a text after receiving a byte array always works.

When `receiveEntireContent` is enabled, then receiving different types should always work. Also double receive of a channel or stream works as well. However,
`receive` executes the whole receive pipeline from the beginning so all content transformations and converters are executed every time that may be slower than with the option disabled.

Comparison:

| receiveEntireContent |same type|different type|channel|
|---------:|:-------:|:------------:|:------|
| enabled  |re-run   |yes, re-run    | yes   |
| disabled |cached same instance|generally no|no|
{ .styled-table}

## Custom types

If a custom content transformation is installed (for example, by intercepting receive pipeline), then a transformed value couldn't be re-received without `receiveEntireContent` option by default. However it is possible to mark a transformed value object as reusable by specifying `reusableValue` option:

```kotlin
val converted = .... // convert somehow from a request payload
proceedWith(ApplicationReceiveRequest(receive.typeInfo, converted, reusableValue = true))
```

## Options

- `receiveEntireContent` When enabled, for every request the whole content will be received and stored as a byte array. This is useful when completely different types need to be received. You also can receive streams and channels. Note that enabling this causes the whole receive pipeline to be executed for every further receive pipeline.