[//]: # (title: Conditional headers)

The [ConditionalHeaders](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-conditional-headers/index.html) plugin (previously known as feature) avoids sending the body of content if it has not changed since the last request. This is achieved by using the following headers:
* The `Last-Modified` response header contains a resource modification time. For example, if the client request contains the `If-Modified-Since` value, Ktor will send a full response only if a resource has been modified after the given date. Note that for [static files](Serving_Static_Content.md) Ktor appends the `Last-Modified` header automatically after [installing](#install_feature) `ConditionalHeaders`.
* The `Etag` response header is an identifier for a specific resource version. For instance, if the client request contains the `If-None-Match` value, Ktor won't send a full response in case this value matches the `Etag`. You can specify the `Etag` value when [configuring](#configure) `ConditionalHeaders`.


## Install ConditionalHeaders {id="install_feature"}
<var name="feature_name" value="ConditionalHeaders"/>
<include src="lib.xml" include-id="install_feature"/>


## Configure headers {id="configure"}

To configure `ConditionalHeaders`, you need to call the [version](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-conditional-headers/-configuration/version.html) function inside the `install` block. This function provides access to a list of resource versions for a given [OutgoingContent](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http.content/-outgoing-content/index.html). You can specify the required versions by using the [EntityTagVersion](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http.content/-entity-tag-version/index.html) and [LastModifiedVersion](https://api.ktor.io/ktor-http/ktor-http/io.ktor.http.content/-last-modified-version/index.html) class objects.

The code snippet below shows how to add a specified `Etag` value for CSS:
```kotlin
install(ConditionalHeaders) {
    version { outgoingContent ->
        when (outgoingContent.contentType?.withoutParameters()) {
            ContentType.Text.CSS -> listOf(EntityTagVersion("123abc"))
            else -> emptyList()
        }
    }
}
```
