[//]: # (title: Request validation)

<var name="plugin_name" value="RequestValidation"/>
<var name="package_name" value="io.ktor.server.plugins.requestvalidation"/>
<var name="artifact_name" value="ktor-server-request-validation"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="request-validation"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
%plugin_name% provides the ability to validate a body of incoming requests.
</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-request-validation/io.ktor.server.plugins.requestvalidation/-request-validation.html) plugin provides the ability to validate a body of incoming requests. You can validate a raw request body or specified request object properties if the `ContentNegotiation` plugin with a [serializer](serialization.md#configure_serializer) is installed. If a request body validation fails, the plugin raises `RequestValidationException`, which can be handled using the [StatusPages](status_pages.md) plugin.



## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Configure %plugin_name% {id="configure"}

Configuring `%plugin_name%` involves three main steps:

1. [Receiving body contents](#receive-body).
2. [Configuring a validation function](#validation-function).
3. [Handling validation exceptions](#validation-exception).

### 1. Receive body {id="receive-body"}

The `%plugin_name%` plugin validates a body of a request if you call the **[receive](requests.md#body_contents)** function with a type parameter. For instance, the code snippet below shows how to receive a body as a `String` value:

```kotlin
```
{src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="52-56,65"}


### 2. Configure a validation function {id="validation-function"}

To validate a request body, use the `validate` function. 
This function returns a `ValidationResult` object representing a successful or unsuccessful validation result.
For an unsuccessful result, **[RequestValidationException](#validation-exception)** is raised.

The `validate` function has two overloads allowing you to validate a request body in two ways:

- The first `validate` overload allows you to access a request body as the object of the specified type.
   The example below shows how to validate a request body representing a `String` value:
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="20-25,43"}

   If you have the `ContentNegotiation` plugin installed configured with a specific [serializer](serialization.md#configure_serializer), you can validate object properties. Learn more from [](#example-object).

- The second `validate` overload accepts `ValidatorBuilder` and allows you to provide custom validation rules. 
   You can learn more from [](#example-byte-array).



### 3. Handle validation exceptions {id="validation-exception"}

If request validation is failed, `%plugin_name%` raises `RequestValidationException`.
This exception allows you to access a request body and get reasons for all validation failures for this request.

You can handle `RequestValidationException` using the [StatusPages](status_pages.md) plugin as follows:

```kotlin
```
{src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="44-48"}

You can find the full example here: [request-validation](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/request-validation).


## Example: Validating object properties {id="example-object"}

In this example, we'll look at how to validate object properties using the `%plugin_name%` plugin.
Suppose the server receives a `POST` request with the following JSON data:

```HTTP
```
{src="snippets/request-validation/post.http" lines="7-14"}

To add validation of the `id` property, follow the steps below:

1. Create the `Customer` data class that describes the JSON object above:
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="14-15"}

2. Install the `ContentNegotiation` plugin with the [JSON serializer](serialization.md#register_json):
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="49-51"}

3. Receive the `Customer` object on the server side as follows:
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="57-60"}
4. In the `%plugin_name%` plugin configuration, add validation of the `id` property to make sure it falls into a specified range:
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="20,26-30,43"}
   
   In this case, `%plugin_name%` will raise **[RequestValidationException](#validation-exception)** if the `id` value is less or equals to `0`.



## Example: Validating byte arrays {id="example-byte-array"}

In this example, we'll take a look at how to validate a request body received as a byte array.
Suppose the server receives a `POST` request with the following text data:

```HTTP
```
{src="snippets/request-validation/post.http" lines="17-20"}

To receive data as a byte array and validate it, perform the following steps:

1. Receive data on the server side as follows:
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="61-64"}
2. To validate the received data, we'll use the second `validate` [function overload](#validation-function) that accepts `ValidatorBuilder` and allows you to provide custom validation rules:
   ```kotlin
   ```
   {src="snippets/request-validation/src/main/kotlin/com/example/Application.kt" lines="20,31-43"}
