# ProGuard

A sample project packed as a JAR using the [Ktor Gradle plugin](https://ktor.io/docs/fatjar.html) and minimized using [ProGuard](https://www.guardsquare.com/manual/home).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

**Proguard** is a tool to shrink, obfuscate, and optimizer for Java bytecode and Android apps, it's the default solution
used in frameworks and projects
like [Compose Desktop](https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md#minification--obfuscation).

Proguard will need rules to keep some classes that use **reflection** and **dynamic access
(dynamic class loading)** to work correctly.

Proguard will give you **warnings** that need to be **either solved or suppressed**. 
This is usually caused by missing class references, Java module that's used yet not included, or when it detects usages
that can't be automated.

Libraries and frameworks might have rules that need to be included to work properly, the following rules are included in
this example:

1. [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines): https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro
2. [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization): https://github.com/Kotlin/kotlinx.serialization/blob/master/rules/common.pro
3. [Proguard](https://github.com/Guardsquare/proguard) Kotlin Example: https://github.com/Guardsquare/proguard/blob/master/examples/application-kotlin/proguard.pro
4. [Google Guava](https://github.com/google/guava): https://github.com/google/guava/wiki/UsingProGuardWithGuava

This example is already configured to work the libraries mentioned above, required modules that are part of the JDK
and some libraries that Ktor server depends on.

> If you're using Ktor Client with OkHttp as an engine in Ktor Server, you need to include
> [OkHttp Proguard](https://square.github.io/okhttp/features/r8_proguard/) Rules, otherwise you will get warnings that
> cause the proguard task build to fail.

If you're using additional libraries that use features such as 
reflections or dynamic class loading, You will usually get warnings that need to be solved, some common ways to solve this:

1. You can see if the library supports Proguard, take a look at the docs, resources of the module you want to include.
`README.md` file, or some common folders such as `proguard` or `rules`, also in `META-INF/proguard` in the JAR file, which
will be included even when not using Proguard if the library has Proguard rules.
2. If the warning says, it can't find a certain class or package from Java,
   you need to update your Proguard configurations
   to either include the required modules or include them all.
   Proguard will need access to the JDK even if your application
   requires installing of JRE to minimize, shrink correctly.
3. You can try to write custom rules, see [Proguard Configurations Usage](https://www.guardsquare.com/manual/configuration/usage)
4. Or you can exclude the library from minimization if it heavily depends on reflections,
   or ignore all the warnings from it, notice it'd not recommended to use `ignorewarnings` as the warnings will catch many common bugs and runtime 
   errors, instead, ignore warnings from a certain package if it doesn't cause any issues

For this example:
- **Fat JAR**: 15.2 MB
- **Minimized JAR with obfuscation disabled**: 9 MB (reduced by 40.79%)

This will be different depending on your project, dependencies, and Proguard configurations.<br>
Generally, you can reduce the size of an application by anything between **20%** and **90%**

For more details on how Proguard works, see [Proguard Manual](https://www.guardsquare.com/manual/home).

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

Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.

> Replace `gradlew` with `gradlew.bat` if you're using **Microsoft Windows**
