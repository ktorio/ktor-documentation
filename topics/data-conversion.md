[//]: # (title: Data conversion)

<include src="lib.md" include-id="outdated_warning"/>

`DataConversion` is a feature that allows to serialize and deserialize a list of values.

By default, it handles primitive types and enums, but it can also be configured to handle additional types. 

If you are using the [Locations feature](locations.md) and want to support
custom types as part of its parameters, you can add new custom converters with this
service.




## Basic installation
{id="basic-installation"}

Installing the DataConversion is pretty easy, and it should be cover primitive types:

```kotlin
install(DataConversion)
```

## Adding converters
{id="adding-converters"}

The DataConversion configuration, provide a `convert<T>` method to define
type conversions. Inside, you have to provide a decoder and an encoder
with the `decode` and `encode` methods accepting callbacks.

* decode callback: `converter: (values: List<String>, type: Type) -> Any?`
  Accepts `values`, a list of strings) representing repeated values in the URL, for example, `a=1&a=2`,
  and accepts the `type` to convert to. It should return the decoded value.
* encode callback: `converter: (value: Any?) -> List<String>` 
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

Another potential use is to customize how a specific enum is serialized. By default enums are serialized and de-serialized
using its `.name` in a case-sensitive fashion. But you can for example serialize them as lower case and deserialize
them in a case-insensitive fashion: 

```kotlin
enum class LocationEnum {
    A, B, C
}

@Location("/") class LocationWithEnum(val e: LocationEnum)

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

## Accessing the service
{id="service"}

You can easily access the DataConversion service, from any call with:

```kotlin
val dataConversion = call.conversionService
```

## The ConversionService interface
{id="interface"}

```kotlin
interface ConversionService {
    fun fromValues(values: List<String>, type: Type): Any?
    fun toValues(value: Any?): List<String>
}
```
{id="ConversionService"}

```kotlin
class DelegatingConversionService(private val klass: KClass<*>) : ConversionService {
    fun decode(converter: (values: List<String>, type: Type) -> Any?)
    fun encode(converter: (value: Any?) -> List<String>)
}
```
{id="DelegatingConversionService"}