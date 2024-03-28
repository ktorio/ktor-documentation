[//]: # (title: Shutdown URL)

<tldr>
<var name="example_name" value="shutdown-url"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

The [ShutDownUrl](https://api.ktor.io/ktor-server/ktor-server-host-common/io.ktor.server.engine/-shut-down-url/index.html) plugin allows you to configure a URL used to shut down the server. 
There are two ways to enable this plugin: 
- In a [configuration file](#config-file).
- By [installing the plugin](#install).



## Configure shutdown URL in a configuration file {id="config-file"}

You can configure a shutdown URL in a [configuration file](server-configuration-file.topic) using the [ktor.deployment.shutdown.url](server-configuration-file.topic#predefined-properties) property.

<tabs group="config">
<tab title="application.conf" group-key="hocon">

```shell
```
{src="snippets/shutdown-url/src/main/resources/application.conf" include-lines="1-2,4-5,9"}

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
```
{src="snippets/shutdown-url/src/main/resources/_application.yaml" include-lines="1-2,4-5"}

</tab>
</tabs>


## Configure shutdown URL by installing the plugin {id="install"}

To [install](server-plugins.md#install) and configure shutdown URL in code, pass `ShutDownUrl.ApplicationCallPlugin` to the `install` function and use the `shutDownUrl` property:

```kotlin
```
{src="snippets/shutdown-url/src/main/kotlin/com/example/Application.kt" include-lines="11-14"}


You can find the full example here: [shutdown-url](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/shutdown-url).