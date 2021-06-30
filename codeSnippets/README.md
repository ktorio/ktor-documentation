# Ktor code examples

This repository contains runnable code examples that show how to work with various Ktor plugins. All examples are stored in the [snippets](snippets) folder and can be run using a **run** Gradle task that depends on an example location. For example, to run an example demonstrating basic HTTP authentication (the [snippets/auth-basic](snippets/auth-basic) folder), execute the following command in a repository's root directory (`codeSnippets`): 
```bash
# macOS/Linux
./gradlew :auth-basic:run

# Windows
gradlew.bat :auth-basic:run
```
Each example has its own `README` file with instructions on how to run it. You can find links to all examples in the [Code examples](https://ktor.io/docs/samples.html) documentation page.

## Referencing code snippets
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