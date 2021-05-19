[//]: # (title: Features)

<include src="lib.md" include-id="outdated_warning"/>

Similar to the server, Ktor supports features on the client. And it has the same design:
there is a pipeline for client HTTP requests, and there are interceptors and installable features.



## Creating custom features

If you want to create features, you can use the [standard features](https://github.com/ktorio/ktor/tree/main/ktor-client/ktor-client-core/common/src/io/ktor/client/features) as a reference.

>You can also check the [HttpRequestPipeline.Phases](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/request/HttpRequestPipeline.kt)
>and [HttpResponsePipeline.Phases](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/statement/HttpResponsePipeline.kt)
>to understand the interception points available.
>
{type="note"}