[//]: # (title: Data conversion)

<include from="lib.topic" element-id="outdated_warning"/>

<var name="artifact_name" value="ktor-server-data-conversion"/>
<var name="package_name" value="io.ktor.server.plugins.dataconversion"/>
<var name="plugin_name" value="DataConversion"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

[%plugin_name%](https://api.ktor.io/ktor-utils/io.ktor.util.converters/-data-conversion/index.html) is a plugin that
allows you to serialize and deserialize a list of values. By default, it handles primitive types and enums, but it can
also be configured to handle additional types. If you are using the [Resources plugin](type-safe-routing.md) and want to
support custom types as part of its parameters, you can add new custom converters with this service.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Add converters {id="add-converters"}

To configure `%plugin_name%`, provide a `convert<T>` method to define type conversions. Inside, you have to provide a
decoder and an encoder with the `decode` and `encode` methods accepting callbacks.

* decode callback: `fromValues(values: List<String>, type: TypeInfo): Any?`
  Accepts `values`, a list of strings representing repeated values in the URL, for example, `a=1&a=2`,
  and accepts the `TypeInfo` to convert to. It should return the decoded value.
* encode callback: `toValues(value: Any?): List<String>`
  Accepts an arbitrary value, and should return a list of strings representing the value.
  When returning a list of a single element, it will be serialized as `key=item1`. For multiple values,
  it will be serialized in the query string as: `samekey=item1&samekey=item2`.

For example:

```kotlin
install(DataConversion) {
    convert<Date> { // this: DelegatingConversionService
        val format = SimpleDateFormat.getInstance()
    
        decode { values, _ -> // converter: (values: List<String>, type: Type) -> Any?
            values.singleOrNull()?.let { format.parse(it) }
        }

        encode { value -> // converter: (value: Any?) -> List<String>
            when (value) {
                null -> listOf()
                is Date -> listOf(SimpleDateFormat.getInstance().format(value))
                else -> throw DataConversionException("Cannot convert $value as Date")
            }
        }
    }
}
```

Another potential use case is to customize how a specific enum is serialized.
By default, enums are serialized and deserialized using its `.name` in a case-sensitive fashion.
But you can, for example, serialize them as a lower case and deserialize
them in a case-insensitive fashion:

```kotlin
enum class LocationEnum {
    A, B, C
}

@Resource("/") class LocationWithEnum(val e: LocationEnum)

@Test fun `location class with custom enum value`() = withLocationsApplication {
    application.install(DataConversion) {
        convert(LocationEnum::class) {
            encode { if (it == null) emptyList() else listOf((it as LocationEnum).name.toLowerCase()) }
            decode { values, type -> LocationEnum.values().first { it.name.toLowerCase() in values } }
        }
    }
    application.routing {
        get<LocationWithEnum> {
            call.respondText(call.locations.resolve<LocationWithEnum>(LocationWithEnum::class, call).e.name)
        }
    }

    urlShouldBeHandled("/?e=a", "A")
    urlShouldBeHandled("/?e=b", "B")
}
```

## Access the service

{id="service"}

You can access the `%plugin_name%` service from any call:

```kotlin
val dataConversion = call.conversionService
```
