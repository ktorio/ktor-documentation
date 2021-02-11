[//]: # (title: Writing your own features)

You can develop your own custom features and reuse them across all your Ktor applications, or you can share them with
the community.

## Concept of features {id="install_feature"}

Every Ktor feature consists of two parts: feature configuration, and the feature itself that you can install. Feature
configuration can be described as any class with mutable fields that can be set by any user of the feature:

```kotlin
```

{src="/snippets/features-api/src/io/ktor/application/newapi/samples/SimpleFeature.kt" include-symbol="MyConfig"}

This configuration then can be then used in the `install` block of your application:

```kotlin
```

{src="/snippets/features-api/src/io/ktor/application/newapi/samples/SimpleFeature.kt" include-symbol="simpleModule"}

Feature itself defines some number of extensions for HTTP call handling process (see below).

## Basic steps to create a new feature

Entry point for creating custom features in Ktor is `makeFeature` method that returns a feature instance that you can
then `install` in your app. In the callback of `makeFeature` you can define how this feature should extend the process
of HTTP handling. In the example below, we create a feature with name `SimpleFeature`, using `MyConfig` configuration
class described above, and extend `receive()` and `respond()` calls in the application:

```kotlin
```
{src="/snippets/features-api/src/io/ktor/application/newapi/samples/SimpleFeature.kt" include-symbol="MyFeature"}

See feature usage below:

```kotlin
```
{src="/snippets/features-api/src/io/ktor/application/newapi/samples/SimpleFeature.kt" include-symbol="usageExample"}

Following basic options are available for feature creation:

* `onReceive` : extends the process of receiving a client's data when call handling.
* `onCall` : extends the handing of the HTTP call itself.
* `onRespond` : extends the process of sending response to a client.

## More advanced options to extend HTTP pipeline

Following DSL functions are also available for advanced customization of the feature's behaviour:

1. `extendCallHandling` supports following callbacks:
    - `monitoring` — defines actions to perform before the call was processed by any feature (including Routing). It is useful for monitoring and logging (see CallLogging feature) to be executed before any "real stuff" was performed with the call because other features can change it's content as well as add more resource usage etc. while for logging and monitoring it is important to observe the pure (untouched) data.
    - `fallback` — defines what to do with the call in case request was not handled for some reason. Usually, it is handy to use `fallback { call -> ...}` to set the response with some message or status code, or to throw an exception.
    - `onCall` — defines what to do with the call that is being currently processed by a server.
2. `extendReceiveHandling` supports following callbacks:
    - `beforeReceive` — defines actions to perform before any transformations were made to the received content. It is useful for caching.
    - `onReceive` — defines how to transform data received from a client.
3. `extendResponseHandling` supports following callbacks:
    - `beforeSend` — allows to use the direct result of call processing (see `onCall`)
    - `onSend` — does transformations of the data. Example: you can write a custom serializer wit h it.
    - `afterSend` — allows to calculate some stats on the data that was sent, or to handle errors. It is used in [Metrics](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/index.html), [CachingHeaders](https://api.ktor.io/%ktor_version%/io.ktor.features/-caching-headers/index.html), [StatusPages](https://api.ktor.io/%ktor_version%/io.ktor.features/-status-pages/index.html).

See the following example of using this DSL:

```kotlin
```
{src="/snippets/features-api/src/io/ktor/application/newapi/samples/AdvancedFeature.kt"}

## Extending execution before/after other feature

Sometimes it's really important to execute a feature before (or after) some other feature was executed.
One of the use cases is when a feature needs to produce data for another feature or needs to use data produced from other feature.

In the example below [RoleBasedAuthorization](https://www.ximedes.com/2020-09-17/role-based-authorization-in-ktor/) feature is introduced. It is executed after [Authentication](https://api.ktor.io/%ktor_version%/io.ktor.auth/-authentication/index.html) feature.

```kotlin
```
{src="/snippets/features-api/src/io/ktor/application/newapi/samples/RoleBasedAuthentication.kt" include-symbol="RoleBasedAuthorization"}

Following options are available:
* `beforeFeature(F)`
* `afterFeature(F)`

> Note: in order to use other feature with `beforeFeature` / `afterFeature` it should inherit an `InterceptionsHolder` interface.


## Managing execution of the HTTP pipeline

For some really advanced use cases it may be required to manage execution of HTTP handling inside feature.
There are following functionality:
* `proceed()` — Continue execution, i.e. stop execution of the current feature and move to the next one
* `finish()` — Finish execution of the current stage (call / receive / respond). No further features will be executed after that.
* `context` — Context of the execution of the current stage (call / receive / respond).
* `proceedWith(context)` — Continue execution of the current stage with new context.
* `call` — Call that is currently being processed.

This methods can be called anywhere inside feature definition.

## Writing custom data serializer

Extending HTTP call handling with a custom data serializer is one of the most popular use case for features in the Ktor reposirory. That is why there is a set of extensions that simplify writing such features:

```kotlin
```
{src="/snippets/features-api/src/io/ktor/application/newapi/SerializationExtensions.kt"}

This extensions can be easily used to create a serializer for some new specific data type:
```kotlin
```
{src="/snippets/features-api/src/io/ktor/application/newapi/samples/SerializationFeature.kt"}