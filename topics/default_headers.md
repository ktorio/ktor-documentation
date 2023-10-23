[//]: # (title: Default headers)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-default-headers"/>
<var name="package_name" value="io.ktor.server.plugins.defaultheaders"/>
<var name="plugin_name" value="DefaultHeaders"/>
<var name="plugin_api_link" value="https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-default-headers/io.ktor.server.plugins.defaultheaders/-default-headers.html"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [%plugin_name%](%plugin_api_link%) plugin adds the standard `Server` and `Date` headers into each response. Moreover, you can provide additional default headers and override the `Server` header.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>


## Configure %plugin_name% {id="configure"}
### Add additional headers {id="add"}
To customize a list of default headers, pass a desired header to `install`  by using the `header(name, value)` function. The `name` parameter accepts an `HttpHeaders` value, for example:
```kotlin
    install(DefaultHeaders) {
        header(HttpHeaders.ETag, "7c876b7e")
    }
```
To add a custom header, pass its name as a string value:
```kotlin
    install(DefaultHeaders) {
        header("Custom-Header", "Some value")
    }
```


### Override headers {id="override"}
To override the `Server` header, use a corresponding `HttpHeaders` value:
```kotlin
    install(DefaultHeaders) {
        header(HttpHeaders.Server, "Custom")
    }
```
Note that the `Date` header is cached due to performance reasons and cannot be overridden by using `%plugin_name%`.
