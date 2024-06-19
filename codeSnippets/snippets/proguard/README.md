# ProGuard

A sample project packed as a JAR using the [Ktor Gradle plugin](https://ktor.io/docs/fatjar.html) and minimized
using [ProGuard](https://www.guardsquare.com/manual/home).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

ProGuard is a tool used to shrink, obfuscate, and optimize Java bytecode and Android applications. It's the default
solution in frameworks and projects such
as [Compose Desktop](https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md#minification--obfuscation).

## Configuration

Certain libraries and frameworks require specific ProGuard rules to function properly. The following rules are included
in this example:

1. [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines): [ProGuard rules](https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro)
2. [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization): [ProGuard rules](https://github.com/Kotlin/kotlinx.serialization/blob/master/rules/common.pro)
3. [Proguard Kotlin Example](https://github.com/Guardsquare/proguard): [ProGuard rules](https://github.com/Guardsquare/proguard/blob/master/examples/application-kotlin/proguard.pro)
4. [Google Guava](https://github.com/google/guava): [ProGuard rules](https://github.com/google/guava/wiki/UsingProGuardWithGuava)

This example is already configured to work the libraries mentioned above, required JDK modules,
and some dependencies of Ktor Server.

> If you're using Ktor Client with OkHttp as an engine in Ktor Server, you need to include
> [OkHttp ProGuard rules](https://square.github.io/okhttp/features/r8_proguard/), otherwise you will get warnings that
> cause the proguard task build to fail.

## Reflection and dynamic access

ProGuard requires specific rules to preserve classes that use reflection and dynamic access (dynamic class loading) to
ensure these features work correctly.

## Handling warnings

ProGuard will generate warnings that are usually caused by missing class references, Java modules being used but not
included, or usages that can't be automated. They must either be resolved or suppressed to maintain proper
functionality.

The following are common ways to solve some warnings:

1. **Check library ProGuard support**

   Verify if the library supports ProGuard by checking in the documentation, resources of the module you want to
   include, the `README.md` file, or common folders such as `proguard` or `rules`. Look in `META-INF/proguard` within
   the JAR file, which will be included if the library has Proguard rules.
2. **Update ProGuard configurations**

   If the warning indicates a missing class or package from Java,
   update your ProGuard configurations to include the required modules or all modules.
   ProGuard will need access to the JDK to minimize and shrink correctly, even if your application only
   requires installing of JRE.
3. **Write custom rules**

   Consider writing custom rules. For more information, refer to
   the [Proguard Configurations Usage](https://www.guardsquare.com/manual/configuration/usage).
4. **Exclude libraries or ignore warnings**

   You can exclude the library from minimization if it heavily depends on reflections,
   or ignore all the warnings specific to it. Avoid using `ignorewarnings`, as warnings often catch
   common bugs and runtime errors. Instead, ignore warnings from certain packages if they don't cause issues.

## JAR size reduction

For this example:

- **Fat JAR**: 15.2 MB
- **Minimized JAR with obfuscation disabled**: 9 MB (reduced by 40.79%)

This reduction will vary depending on your project, dependencies, and ProGuard configurations.
Typically, you can reduce the size of an application by anywhere between **20%** and **90%**.

For more details on how ProGuard works, see the [Proguard Manual](https://www.guardsquare.com/manual/home).

## Running

To build a minimized JAR and run the application, execute the following commands:

```bash
./gradlew :proguard:buildMinimizedJar
java -jar snippets/proguard/build/libs/my-application.min.jar
```

or run the JAR directly using a Gradle task:

```bash
./gradlew :proguard:runMinimizedJar
```

> On **Microsoft Windows**, replace `gradlew` with `gradlew.bat`.

Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.

