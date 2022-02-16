[//]: # (title: Custom plugins - Base API)

<microformat>
<var name="example_name" value="custom-plugin-base-api"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<include src="lib.xml" include-id="outdated_warning"/>

You can develop your own plugins and reuse them across all your Ktor applications, or you can share them with the community.
A typical plugin has the following structure:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/CustomHeader.kt"}

`CustomPlugin` is a plugin instance class, which should be immutable to avoid unintended side effects in a highly concurrent environment.
Plugin implementation should be thread-safe as it will be called from multiple threads.

The `Configuration` instance is handed to the user installation script, allowing plugin configuration usually containing mutable properties and configuration methods.

The `Plugin` companion object conforms the `ApplicationPlugin` interface and acts as a glue to construct the actual `CustomPlugin` with the right `Configuration`.

A custom plugin can be installed normally with the standard `install` function:

```kotlin
```
{src="snippets/custom-plugin-base-api/src/main/kotlin/com/example/PluginApplication.kt" lines="9-12"}
