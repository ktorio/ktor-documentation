[//]: # (title: Creating custom plugins)

<microformat>
<var name="example_name" value="custom-feature"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<include src="lib.xml" include-id="outdated_warning"/>

You can develop your own plugins (formerly known as features) and reuse them across all your Ktor applications, or you can share them with the community.
A typical plugin has the following structure:

```kotlin
class CustomFeature(configuration: Configuration) {
    val prop = configuration.prop // Copies a snapshot of the mutable config into an immutable property.
    
    class Configuration {
       var prop = "value" // Mutable property.
    }

    // Implements ApplicationFeature as a companion object.
    companion object Feature : ApplicationFeature<ApplicationCallPipeline, CustomFeature.Configuration, CustomFeature> {
       // Creates a unique key for the feature.
       override val key = AttributeKey<CustomFeature>("CustomFeature")
       
       // Code to execute when installing the plugin.
       override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomFeature {
           
           // It is responsibility of the install code to call the `configure` method with the mutable configuration.
           val configuration = CustomFeature.Configuration().apply(configure)
           
           // Create the plugin, providing the mutable configuration so the plugin reads it keeping an immutable copy of the properties. 
           val feature = CustomFeature(configuration)
           
           // Intercept a pipeline.
           pipeline.intercept(…) { 
                // Perform things in that interception point.
           }
           return feature
       }
    }
}
```

`CustomFeature` is a plugin instance class, which should be immutable to avoid unintended side-effects in a highly concurrent environment.
Plugin implementation should be thread-safe as it will be called from multiple threads.

The `Configuration` instance is handed to the user installation script, allowing plugin configuration usually containing mutable properties and configuration methods.

The `Feature` companion object conforms the `ApplicationFeature` interface and acts as a glue to construct the actual `CustomFeature` with the right `Configuration`.

A custom plugin can be installed normally with the standard `install` function:

```kotlin
fun Application.main() {
    install(CustomFeature) { // Install a custom plugin
        prop = "Hello" // configuration script
    }
}
```
