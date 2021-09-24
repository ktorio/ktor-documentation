[//]: # (title: Partial content)

<include src="lib.xml" include-id="outdated_warning"/>

<var name="artifact_name" value="ktor-server-partial-content"/>
<var name="plugin_name" value="PartialContent"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
</microformat>

The `%plugin_name%` plugin adds support for handling Partial Content requests - requests with the `Range` header. 
It intercepts the generated response adding the `Accept-Ranges` and the `Content-Range` header and slicing the served content when required.

Partial Content is well-suited for streaming content or resume partial downloads with
download managers, or in unreliable networks.

It is especially useful for the [Static Content Plugin](Serving_Static_Content.md).


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

## Configuration

```kotlin
install(PartialContent) {
    // Maximum number of ranges that will be accepted from an HTTP request.
    // If the HTTP request specifies more ranges, they will all be merged into a single range.
    maxRangeCount = 10
}
```

## Detailed description

This plugin only works with *HEAD* and *GET* requests.
And it will return a `405 Method Not Allowed` if the client tries to use the `Range`
header with other methods.

It disables compression when serving ranges.

It is only enabled for responses that define the `Content-Length`. And it:

* Removes the `Content-Length` header
* Adds the `Accept-Ranges` header 
* Adds the `Content-Range` header with the requested Ranges
* Serves only the requested slice of the content

>It should work with any served content of the type `OutgoingContent.ReadChannelContent`
>as long as its length is defined, like for example the `LocalFileContent`.
>
{type="note"}

>This HTTP mechanism for Partial Content is described in the [RFC-7233](https://tools.ietf.org/html/rfc7233#section-4.1).
>
{type="note"}