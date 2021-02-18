# Ktor Code Samples

This repository contains runnable code examples that show how to work with various Ktor features. All examples are stored in the [snippets](snippets) folder and can be run using a **run** Gradle task that depends on an example location. For example, to run an example demonstrating the Authentication feature (the [snippets/auth](snippets/auth) folder), execute the following command in a repository's root directory (`codeSnippets`): 
```bash
./gradlew :auth:run
```
Each sample has its own `README` file with instructions on how to run it. You can find links to all samples in the [Samples](https://ktor.io/docs/samples.html) documentation page.

## Referencing Code Snippets
To display a specific source file in a Markdown topic, use the `src` attribute as follows:
````
```kotlin
```
{src="/snippets/autohead/src/AutoHead.kt"}
````
If you want to display only a specific function from this source file, use the `include-symbol` attribute:
````
```kotlin
```
{src="/snippets/autohead/src/AutoHead.kt" include-symbol="main"}
````