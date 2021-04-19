[//]: # (title: Creating custom features)

<microformat>
<var name="example_name" value="custom-feature"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

<include src="lib.md" include-id="outdated_warning"/>

You can develop your own features and reuse them across all your Ktor applications, or you can share them with the community.
A typical feature has the following structure:

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
       
       // Code to execute when installing the feature.
       override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): CustomFeature {
           
           // It is responsibility of the install code to call the `configure` method with the mutable configuration.
           val configuration = CustomFeature.Configuration().apply(configure)
           
           // Create the feature, providing the mutable configuration so the feature reads it keeping an immutable copy of the properties. 
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

`CustomFeature` is a feature instance class, which should be immutable to avoid unintended side-effects in a highly concurrent environment.
Feature implementation should be thread-safe as it will be called from multiple threads.

The `Configuration` instance is handed to the user installation script, allowing feature configuration usually containing mutable properties and configuration methods.

The `Feature` companion object conforms the `ApplicationFeature` interface and acts as a glue to construct the actual `CustomFeature` with the right `Configuration`.

A custom feature can be installed normally with the standard `install` function:

```kotlin
fun Application.main() {
    install(CustomFeature) { // Install a custom feature
        prop = "Hello" // configuration script
    }
}
```
