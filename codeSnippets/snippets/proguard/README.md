# ProGuard

A sample project packed as a JAR using the [Ktor Gradle plugin](https://ktor.io/docs/fatjar.html) and minimized using [ProGuard](https://www.guardsquare.com/manual/home).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To build a minimized JAR and run the application, execute the following commands:
```bash
./gradlew :proguard:minimizedJar
java -jar snippets/proguard/build/libs/my-application.min.jar
```

Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.
