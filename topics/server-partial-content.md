[//]: # (title: Partial content)

<primary-label ref="server-plugin"/>

<var name="artifact_name" value="ktor-server-partial-content"/>
<var name="package_name" value="io.ktor.server.plugins.partialcontent"/>
<var name="plugin_name" value="PartialContent"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<p>
<b>Server example</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/download-file">download-file</a>,
<b>client example</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-download-file-range">client-download-file-range</a>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-partial-content/io.ktor.server.plugins.partialcontent/-partial-content.html) plugin adds support for handling [HTTP range requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/Range_requests) used to send only a portion of an HTTP message back to a client. This plugin is useful for streaming content or resuming partial downloads.

`%plugin_name%` has the following limitations:
- Works only for `HEAD` and `GET` requests and returns `405 Method Not Allowed` if the client tries to use the `Range` header with other methods.
- Works only for responses that have the `Content-Length` header defined.
- Disables [Compression](server-compression.md) when serving ranges.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>

To learn how to use `%plugin_name%` to serve a file using HTTP range requests, see the [](server-responses.md#file) section.
