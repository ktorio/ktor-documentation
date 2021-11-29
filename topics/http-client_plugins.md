[//]: # (title: Plugins)

<include src="lib.xml" include-id="outdated_warning"/>

Similar to the server, Ktor supports plugins on the client. And it has the same design:
there is a pipeline for client HTTP requests, and there are interceptors and installable plugins.



## Creating custom plugins

If you want to create plugins, you can use the [standard plugins](https://github.com/ktorio/ktor/tree/main/ktor-client/ktor-client-core/common/src/io/ktor/client/plugins) as a reference.

>You can also check the [HttpRequestPipeline.Phases](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/request/HttpRequestPipeline.kt)
>and [HttpResponsePipeline.Phases](https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/statement/HttpResponsePipeline.kt)
>to understand the interception points available.
>
{type="note"}
