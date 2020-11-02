[//]: # (title: Gradle)
[//]: # (caption: Setting up a Gradle Build)
[//]: # (category: quickstart)
[//]: # (toc: true)
[//]: # (permalink: /quickstart/quickstart/gradle.html)
[//]: # (redirect_from: redirect_from)
[//]: # (- /quickstart/gradle.html: - /quickstart/gradle.html)
[//]: # (- /quickstart/quickstart/intellij-idea/gradle.html: - /quickstart/quickstart/intellij-idea/gradle.html)
[//]: # (- /quickstart/quickstart/intellij-idea/plugin.html: - /quickstart/quickstart/intellij-idea/plugin.html)

In this guide, we will show you how to create a `build.gradle` file
and how to configure it to support Ktor.





## Basic Kotlin `build.gradle` file (without Ktor)
{id="initial"}

First of all, you need a skeleton `build.gradle` file including Kotlin.
You can create it with any text editor, or you can use IntelliJ to create
it following the [IntelliJ guide](/quickstart/quickstart/intellij-idea.html).

The initial file looks like this:


```groovy
group 'Example'
version '1.0-SNAPSHOT'

buildscript {
    ext.kotlin_version = '%kotlin_version%'

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'java'
apply plugin: 'kotlin'

sourceCompatibility = 1.8

repositories {
    jcenter()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

## Add Ktor dependencies and configure build settings
{id="ktor-dependencies"}

Ktor artifacts are located in a specific repository on bintray.
And its core has dependencies on the `kotlinx.coroutines` library that
can be found on `jcenter`.

You have to add both to the `repositories` block in the `build.gradle` file:

```groovy
jcenter()
```

You have to specify that version in each Ktor artifact reference,
and to avoid repetitions, you can specify that version in an extra property
in the `buildscript` block (or in a `gradle.properties` file) for using it later:

```groovy
ext.ktor_version = '%ktor_version%'
```

Now you have to add the `ktor-server-core` artifact, referencing the `ktor_version` you specified:

```groovy
compile "io.ktor:ktor-server-core:$ktor_version"
```

>In groovy, there are single-quoted strings (instead of characters)
>and double-quoted strings, to be able to interpolate variables like
>versions, you have to use double-quoted strings.
>
{type="note" id="java8"}

You need to tell the Kotlin compiler to generate bytecode compatible with Java 8:

```groovy
compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
```

## Choose your engine and configure it
{id="engine"}

Ktor can run in many environments, such as Netty, Jetty or any other
Servlet-compatible Application Container such as Tomcat.

This example shows you how to configure Ktor with Netty.
For other engines see [artifacts](/quickstart/artifacts.html) for a list of
available artifacts.

You will add a dependency for `ktor-server-netty` using the
`ktor_version` property you have created. This module provides
a Netty web server and all the required code to run Ktor
application on top of it:

```groovy
compile "io.ktor:ktor-server-netty:$ktor_version"
```

## Final `build.gradle` (with Ktor)
{id="complete"}

When you are done, the `build.gradle` file should look like this:


```groovy
group 'Example'
version '1.0-SNAPSHOT'

buildscript {
    ext.kotlin_version = '%kotlin_version%'
    ext.ktor_version = '%ktor_version%'

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'java'
apply plugin: 'kotlin'

sourceCompatibility = 1.8
compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    experimental {
        coroutines "enable"
    }
}

repositories {
    jcenter()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    compile "io.ktor:ktor-server-netty:$ktor_version"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

You can now run Gradle (just `gradle` or `./gradlew` if using the wrapper)
to fetch dependencies and verify everything is set up correctly.

This tutorial will guide you from the most basic setup through to a full
featured setup you can use to start developing your app.

## IntelliJ: Prerequisites

1.  The most recent version of IntelliJ IDEA
2.  Kotlin and Gradle plugins enabled (They should be enabled by default.)

You can check this in IntelliJ IDEA in the main menu:
* Windows: `File -> Settings -> Plugins`
* Mac: `IntelliJ IDEA -> Settings -> Plugins`

## IntelliJ: Start a Project

1.  `File -> New -> Project`:

    ![Ktor IntelliJ: File New Project](file-new-project.png)

2.  Select Gradle and under Additional Libraries and Frameworks, check Java and Kotlin (Java).  Confirm that Project SDK is completed and click `Next`:

    ![Ktor IntelliJ: Gradle Kotlin JVM](gradle-kotlin-jvm.png)

3.  Enter a GroupId: `Example`
    and ArtifactId: `Example`
    and click Next:

    ![Ktor IntelliJ: GroupId](groupid.png)

4.  Complete Project name: `Example`
    and Project location: `a/path/on/your/filesystem`
    and click `Finish`:

    ![Ktor IntelliJ: Project Location Name](project-location-name.png)

5.  Wait a few seconds for Gradle to run, and you should see a project structure like the following (with a few other files and directories):

    ![Ktor IntelliJ: Project Structure](project-structure.png)

6.  Update your `build.gradle` file with the artifact and repositories for the classes to be available:
    * Include `compile("io.ktor:ktor-server-netty:$ktor_version")`, in your `build.gradle`'s `dependencies` block
    * Include  `jcenter()` in your `repositories` block

    ![Ktor IntelliJ: Build Gradle](build-gradle.png)

For a more detailed guide on setting up the `build.gradle` file, check the [Getting Started with Gradle](/quickstart/quickstart/gradle.html) section. 

>When auto-import options, shows up (likely on bottom right hand side) click allow auto-import. 
>
{type="note"}

## IntelliJ: Gradle Setup

>This section assumes you have some basic knowledge of Gradle. If you have never used Gradle,
>gradle.org provides [several guides](https://guides.gradle.org/building-java-applications/) to help you get started.
>
{type="note"}

You can set-up a simple Ktor application using Gradle like this:

![Ktor Build with Gradle](ktor_build_gradle.png)


```kotlin
// build.gradle.kts

import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "Example"
version = "1.0-SNAPSHOT"

val ktor_version = "%ktor_version%"

plugins {
    application
    kotlin("jvm") version "%kotlin_version%"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:1.2.3")
    testCompile(group = "junit", name = "junit", version = "4.12")
}
```



```groovy
// build.gradle

group 'Example'
version '1.0-SNAPSHOT'

buildscript {
    ext.kotlin_version = '%kotlin_version%'
    ext.ktor_version = '%ktor_version%'

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'java'
apply plugin: 'kotlin'
apply plugin: 'application'

mainClassName = 'MainKt'

sourceCompatibility = 1.8
compileKotlin { kotlinOptions.jvmTarget = "1.8" }
compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }

repositories {
    mavenCentral()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    compile "io.ktor:ktor-server-netty:$ktor_version"
    compile "ch.qos.logback:logback-classic:1.2.3"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```


Text version:


Since Ktor is not yet 1.0, we have custom Maven repositories for distributing our early preview artifacts.
You have to set up a couple of repositories as shown below, so your tools can find Ktor artifacts and dependencies.

Of course, don't forget to include the actual artifact! For our quickstart, we are using the `ktor-server-netty` artifact.
That includes Ktor's core, netty, and the ktor-netty connector as transitive dependencies.
You can, of course, include any additional dependencies that you need.

Since Ktor is designed to be modular, you will require additional artifacts and potentially other repositories
for specific features. You can find the required artifacts (and repositories where required) for each feature in the
specific feature documentation.
{.note}

## IntelliJ: Create the App

Select the `src/main/kotlin` directory and create a new package.  We will call it `blog`.

Select that directory and create a new kotlin file under it named `BlogApp`

![Ktor IntelliJ: Create Kotlin File](create-kotlin-file.png)

![Ktor IntelliJ: Create Kotlin File Name](create-kotlin-file-name.png)

Copy and paste in the most basic setup for an app so that it looks like:


```kotlin
package blog

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("My Example Blog", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
```

![Ktor IntelliJ: Program](program.png)

Now you can Run '`blog.BlogAppKt`'. You can do it, by pressing the glutter icon with the **🐞**{ style="transform:rotate(90deg);display:inline-block;"} symbol and selecting `Debug 'blog.BlogAppKt'`:

![Ktor IntelliJ: Program Run](program-run.png)

This will also create a run configuration in the upper-right part of IntelliJ, that will allow running
this configuration again easily:

![Ktor IntelliJ: Program Run Config](program-run-config.png)

This will start the Netty web server.
In your browser enter the URL:  localhost:8080
And you should see your example blog page.

![Ktor IntelliJ: Website](website.png)

## IntelliJ: Improve the app with the Application object

The setup above has a lot of nested blocks and is not ideal for starting to 
add functionality to your app.  We can improve it by using the Application object 
and referring to that from an embeddedServer call in the main function.  

Change your code in BlogApp.kt to the following to try this:


```kotlin
package blog

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.respondText("My Example Blog2", ContentType.Text.Html)
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080, watchPaths = listOf("BlogAppKt"), module = Application::module).start()
}
```

## IntelliJ: Extract out Configuration Data

Although we can designate some application configuration data in the main function embeddedServer call, we can provide more flexibility for future deployments and changes by extracting this out to a separate configuration file.  In the `src/main/resources` directory we will create a new text file named `application.conf` with the following content:


```kotlin
ktor {
    deployment {
        port = 8080
    }

    application {
        modules = [ blog.BlogAppKt.main ]
    }
}
```

Then we delete the main function from `BlogApp.kt` and change `fun Application.module()` to `fun Application.main()`.  However, if we run the application now, it will fail with an error message like "Top-level function 'main' not found in package blog."  Our `Application.main()` function is now a function extension and does not qualify as a top-level main function.   

This requires us to indicate a new main class as IntelliJ IDEA will no longer be able to find it automatically.  In `build.gradle` we add:


```groovy
// build.gradle

apply plugin: 'application'

//mainClassName = 'io.ktor.server.netty.DevelopmentEngine' // For versions < 1.0.0-beta-3
mainClassName = 'io.ktor.server.netty.EngineMain' // Starting with 1.0.0-beta-3
```



```kotlin
// build.gradle.kts

plugins {
    application
    // ...
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}
```




And then go to `Run -> Edit Configurations` select the `blog.BlogAppKt` configuration and change its Main class to:
`io.ktor.server.netty.EngineMain`

Now when we run the new configuration, the application will start again.

## Configure logging
{id="logging"}

If you want to log application events and useful information,
you can read about it further in the [logging](/servers/logging.html) page.