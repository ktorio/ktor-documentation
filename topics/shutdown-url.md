[//]: # (title: Shutdown URL)

<tldr>
<var name="example_name" value="shutdown-url"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

The [ShutDownUrl](https://api.ktor.io/ktor-server/ktor-server-host-common/io.ktor.server.engine/-shut-down-url/index.html) plugin allows you to configure a URL used to shut down the server. 
There are two ways to enable this plugin: 
- In a [configuration file](#config-file).
- By [installing the plugin](#install).



## Configure shutdown URL in a configuration file {id="config-file"}

You can configure a shutdown URL in a [configuration file](Configurations.xml#configuration-file) using the [ktor.deployment.shutdown.url](Configurations.xml#predefined-properties) property.

<tabs group="config">
<tab title="application.conf" group-key="hocon">

```shell
```
{src="snippets/shutdown-url/src/main/resources/application.conf" lines="1-2,4-5,9"}

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
```
{src="snippets/shutdown-url/src/main/resources/_application.yaml" lines="1-2,4-5"}

</tab>
</tabs>


## Configure shutdown URL by installing the plugin {id="install"}

To [install](Plugins.md#install) and configure shutdown URL in code, pass `ShutDownUrl.ApplicationCallPlugin` to the `install` function and use the `shutDownUrl` property:

```kotlin
```
{src="snippets/shutdown-url/src/main/kotlin/com/example/Application.kt" lines="11-14"}


You can find the full example here: [shutdown-url](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/shutdown-url).