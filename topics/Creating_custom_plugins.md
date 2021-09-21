[//]: # (title: Creating custom plugins)

<microformat>
<var name="example_name" value="custom-plugin"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<include src="lib.xml" include-id="outdated_warning"/>

You can develop your own plugins and reuse them across all your Ktor applications, or you can share them with the community.
A typical plugin has the following structure:

```kotlin
class CustomPlugin(configuration: Configuration) {
    val prop = configuration.prop // Copies a snapshot of the mutable config into an immutable property.
    
    class Configuration {
       var prop = "value" // Mutable property.
    }

    // Implements ApplicationPlugin as a companion object.
    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, CustomPlugin.Configuration, CustomPlugin> {
       // Creates a unique key for the plugin.
       override val key = AttributeKey<CustomPlugin>("CustomPlugin")
       
       // Code to execute when installing the plugin.
       override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomPlugin {
           
           // It is responsibility of the install code to call the `configure` method with the mutable configuration.
           val configuration = CustomPlugin.Configuration().apply(configure)
           
           // Create the plugin, providing the mutable configuration so the plugin reads it keeping an immutable copy of the properties. 
           val plugin = CustomPlugin(configuration)
           
           // Intercept a pipeline.
           pipeline.intercept(…) { 
                // Perform things in that interception point.
           }
           return plugin
       }
    }
}
```

`CustomPlugin` is a plugin instance class, which should be immutable to avoid unintended side effects in a highly concurrent environment.
Plugin implementation should be thread-safe as it will be called from multiple threads.

The `Configuration` instance is handed to the user installation script, allowing plugin configuration usually containing mutable properties and configuration methods.

The `Plugin` companion object conforms the `ApplicationPlugin` interface and acts as a glue to construct the actual `CustomPlugin` with the right `Configuration`.

A custom plugin can be installed normally with the standard `install` function:

```kotlin
fun Application.main() {
    install(CustomPlugin) { // Install a custom plugin
        prop = "Hello" // configuration script
    }
}
```
