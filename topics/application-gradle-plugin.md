[//]: # (title: Distribution via Gradle Application plugin)

From the Gradle [documentation](https://docs.gradle.org/current/userguide/application_plugin.html):
> The Application plugin facilitates creating an executable JVM application. 
> It makes it easy to start the application locally during development, 
> and to package the application as a TAR and/or ZIP including operating system specific start scripts.

We will use the second responsibility of this plugin and show how to package a simple Ktor application.

Let's start with the following project structure:

```text
├── build.gradle
├── settings.gradle
└── src
    └── main
        ├── kotlin
        └── resources
├── gradle
├── gradlew
└── gradlew.bat
```
You can use the [wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:adding_wrapper) task to generate `gradle`, `gradlew` and `gradlew.bat` files.

Here is the initial content for the build configuration:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" interpolate-variables="true" src="snippets/gradle-app/initial.gradle" />
    </tab>
</tabs>

To use Ktor we need to add corresponding dependencies:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" interpolate-variables="true" src="snippets/gradle-app/ktor-deps.gradle" />
    </tab>
</tabs>

Then create a file with the simplest possible code for our server application:
<code style="block" lang="Kotlin" src="snippets/gradle-app/kotlin/app.kt" />

Now we need to run our application to make sure it works. We can do it by clicking on a [gutter icon](https://www.jetbrains.com/help/idea/settings-gutter-icons.html) in Intellij IDEA or 
by building project and running it with `java`.

The next step is to actually apply the Gradle Application plugin and set the main class for it:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" src="snippets/gradle-app/plugin.gradle" />
    </tab>
</tabs>

Finally, run `distZip` or `distTar` task to package our application:

<tabs>
    <tab title="Linux/MacOS">
        <code style="block" lang="Bash">./gradlew distZip</code>
    </tab>
    <tab title="Windows">
        <code style="block" lang="CMD">gradlew.bat distZip</code>
    </tab>
</tabs>

By default, the plugin places the resulting archive in the `build/distributions` directory. 
To run the application after archive distribution, extract its contents and 
execute the start script from the `bin` directory for your OS.