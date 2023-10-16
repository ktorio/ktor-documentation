[//]: # (title: Data conversion)

<var name="artifact_name" value="ktor-server-data-conversion"/>
<var name="package_name" value="io.ktor.server.plugins.dataconversion"/>
<var name="plugin_name" value="DataConversion"/>
<var name="example_name" value="data-conversion"/>

<tldr>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
The %plugin_name% plugin for the Ktor server allows you to add custom converters for serializing and deserializing a list of values.
</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-utils/io.ktor.util.converters/-data-conversion/index.html) plugin
allows you to serialize and deserialize a list of values. By default, Ktor handles primitive types and enums through the
[DefaultConversionService](https://api.ktor.io/ktor-utils/io.ktor.util.converters/-default-conversion-service/index.html).
You can extend this service to handle additional types by installing and configuring the `%plugin_name%` plugin.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Add converters {id="add-converters"}

You can define type conversions within the `%plugin_name%` configuration. Provide a `convert<T>` method for the
specified type and use the available
functions to serialize and deserialize a list of values:

* Use the `decode()` function to deserialize a list of values. It takes a list of strings, representing
  repeated values in the URL and returns the decoded value.

  ```kotlin
  decode { values -> // converter: (values: List<String>) -> Any?
    //deserialize values
  }
  ```

* Use the `encode()` function to serialize a value. The function takes an arbitrary value and returns a list of
  strings representing it.

  ```kotlin
     encode { value -> // converter: (value: Any?) -> List<String>
       //serialize value
      }
  ```

## Access the service

{id="service"}

You can access the `%plugin_name%` service from the current context:

```kotlin
val dataConversion = application.conversionService
```

You can then use the converter service to call the callback functions:

* The `fromValues(values: List<String>, type: TypeInfo)` callback function accepts `values` as a list of strings, and
  the `TypeInfo` to convert the value to
  and returns the decoded value.
* The `toValues(value: Any?)` callback function accepts an arbitrary value and returns a list of strings representing
  it.

## Example

In the following example, a converter for the type `LocalDate` is defined and configured to serialize and deserialize
values. When the `encode` function is called, the service will convert the value using a `SimpleDateFormat` and return a
list containing the formatted value.
When the `decode` function is called, the service will format the date as a `LocalDate` and return it.

```kotlin
```

{src="snippets/data-conversion/src/main/kotlin/dataconversion/Application.kt" include-lines="18-34"}

The conversion service can then be called manually to retrieve the encoded and decoded values:

```kotlin
```

{src="snippets/data-conversion/src/main/kotlin/dataconversion/Application.kt" include-lines="38-39"}

For the full example,
see [%example_name%](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/%example_name%)