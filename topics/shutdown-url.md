[//]: # (title: Shutdown URL)

<microformat>
<var name="example_name" value="shutdown-url"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `ShutDownUrl` plugin allows you to configure a URL used to shut down the server. 
There are two ways to enable this plugin: 
- In a [HOCON](#hocon) configuration file.
- By [installing the plugin](#install).



## Configure shutdown URL in HOCON {id="hocon"}

You can configure a shutdown URL in a [HOCON](Configurations.xml#hocon-file) configuration file using the [ktor.deployment.shutdown.url](Configurations.xml#predefined-properties) property.

```kotlin
```
{src="snippets/shutdown-url/src/main/resources/application.conf" lines="1-2,4-5,9"}

## Configure shutdown URL by installing the plugin {id="install"}

To [install](Plugins.md#install) and configure shutdown URL in code, pass `ShutDownUrl.ApplicationCallPlugin` to the `install` function and use the `shutDownUrl` property:

```kotlin
```
{src="snippets/shutdown-url/src/main/kotlin/com/example/Application.kt" lines="11-14"}


You can find the full example here: [shutdown-url](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/shutdown-url).