# ProGuard

A sample project packed as a JAR using the [Ktor Gradle plugin](https://ktor.io/docs/fatjar.html) and minimized using [ProGuard](https://www.guardsquare.com/manual/home).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

This example is configured to work with Kotlinx coroutines and serialization, required modules that are part of the JDK
and some libraries that Ktor server depends on.

if you're using additional libraries that use features such as 
reflections or dynamic class loading, You will get a warning that needs to be solved.

For this example, the Fat JAR size is (15.2 MB) and the minimized JAR (9 MB). This will
be different depending on your project, dependencies, and Proguard configurations.

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
