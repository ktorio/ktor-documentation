# Get started with Ktor Server

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="server-get-started"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to open, run and test a server application with Ktor.
</link-summary>

In this section, you will learn how to create, open and run
your first Ktor server project. Once you get up and running, you can attempt a series of tasks to familiarize yourself
with Ktor.

## Create a new Ktor project {id="create-project"}

The simplest way to create a new Ktor project is by using
the [Ktor project generator](https://start.ktor.io/?_ga=2.179570276.13899987.1704363276-861017825.1700565260&_gl=1*422eap*_ga*ODYxMDE3ODI1LjE3MDA1NjUyNjA.*_ga_9J976DJZ68*MTcwNDM3MTUzOC40OS4xLjE3MDQzNzI5NDMuNTQuMC4w#/settings?name=ktor-sample-app&website=example.com&artifact=com.example.ktor-sample&kotlinVersion=1.9.22&ktorVersion=2.3.7&buildSystem=GRADLE&engine=NETTY&configurationIn=CODE&addSampleCode=true&plugins=).

Alternatively, you can [create a new Ktor project with the plugin for IntelliJ IDEA Ultimate](intellij-idea.topic).

> Unless explicitly stated we don’t assume you have changed any settings in the Ktor Project Generator. Nor do we assume
> that you have added any optional plugins.
>
{style="note"}

## Unpack and run the project {id="unpacking"}

This is the process to unpack, build and run the project from the command line. The descriptions below assume that:

- You have created and downloaded a project called `ktor-sample-app`.
- This has been placed in a folder called `myprojects` in your home directory.

Please alter the names and paths as required to match your own setup.

<procedure>
  <step>
  <p>Navigate to the <code>myprojects</code> folder in the home directory.</p>
  <code-block prompt="tmp> " lang="console">
    cd ~/myprojects
  </code-block>
 <code-block prompt="myprojects> " lang="console">
  </code-block>
  </step>
 <step>
  <p>Ensure the only thing in this folder is the ZIP archive.</p>
  <code-block prompt="myprojects> " lang="console">
    ls
  </code-block>
 <code-block prompt="myprojects> " lang="console">
    ktor-sample-app.zip
  </code-block>
  </step>
 <step>
  <p>Unpack the ZIP archive into a folder of the same name.</p>
  <tabs>
    <tab title="macOS" group-key="macOS">
    <code-block prompt="myprojects> " lang="console">
      unzip ktor-sample-app.zip  -d ktor-sample-app
    </code-block>
    </tab>
    <tab title="Windows" group-key="windows">
      <code-block prompt="myprojects> " lang="console">
        tar -xf ktor-sample-app.zip
      </code-block>
    </tab>
  </tabs>
  <p>Your directory will now contain the ZIP archive and the unpacked folder.</p>
  </step>
  <step>
    <p>Change the directory into the newly created folder.</p>
    <code-block prompt="myprojects> " lang="console">
        cd ktor-sample-app
      </code-block>
  </step>
  <step>
  <p>Use the <code>chmod</code> command to make the gradlew Gradle helper script executable.</p>
   <code-block prompt="ktor-sample-app> " lang="console">
    chmod +x ./gradlew
  </code-block>
  </step>
  <step>
  <p>You can verify that the file is executable by examining the first word of output from <code>ls -l</code>.
  The 4th, 7th and 10th characters should be an ‘x’.</p>
   <code-block prompt="ktor-sample-app> " lang="console">
    ls -l gradlew | cut -d " " -f 1
  </code-block>
  <p>Now that the <code>gradlew</code> script is executable, you can build the project via the Gradle build tool.</p>
  </step>
  <step>
  <p>To build the project, use the following command:</p>
   <code-block prompt="ktor-sample-app> " lang="console">
    ./gradlew build
  </code-block>
  <p>If you see that your build has been successful you can execute the project, again via Gradle.</p>
  </step>
  <step>
    <p>To run the project, use the following command:</p>
   <code-block prompt="ktor-sample-app> " lang="console">
    ./gradlew run
  </code-block>
  <p>You can verify the project is running by opening a browser at the URL mentioned in the output (<a href="http://0.0.0.0:8080"/>).</p>
  </step>
</procedure>

Congratulations! You have successfully started your Ktor project.

<p>Note that the command line is unresponsive because the underlying process is busy running the Ktor application. You can
type <shortcut>CTRL+C</shortcut> to cancel the application.</p>

## Open, explore and run in IntelliJ IDEA {id="open-explore-run"}

### Open the project {id="open"}

If you have [IntelliJ IDEA](https://www.jetbrains.com/idea/) installed, you can easily open the project from the command
line.

Make sure you are in the project folder and then type the `idea` command, followed by a period to represent the current
folder:

```Bash
idea .
```

{prompt="ktor-sample-app>"}

Alternatively, you can launch IntelliJ IDEA yourself and then select the open command, either from the Welcome Screen or
from the File Menu. You can then navigate to the `ktor-sample-app` folder and select it. See [the IntelliJ IDEA
documentation](https://www.jetbrains.com/help/idea/creating-and-managing-projects.html) for more details on managing
projects.

### Explore the project {id="explore"}

Whichever option you choose the project should open as shown below:

[//]: # (include screenshot)

In order to explain the project layout we have expanded the structure in the Project View and selected the
file `settings-gradle.kts`.

[//]: # (include screenshot)

You will see that the code to run your application lives in packages under `src/main/kotlin`. The default package is
called `com.example` and contains a subpackage called `plugins`.
Two files have been created within these packages, named `Application.kt` and `Routing.kt`.

[//]: # (include screenshot)

The name of the project is configured in `settings-gradle.kts`.

[//]: # (include screenshot)

Configuration files, and other kinds of content, live within the `src/main/resources` folder.

[//]: # (include screenshot)

A skeleton test has been created in a package under `src/test/kotlin`.

[//]: # (include screenshot)

### Run the project {id="run"}

<procedure>
  <p>To run the project from within IntelliJ IDEA:</p>
  <step>

[//]: # (include elephant icon)

  <p>Open the <a href="https://www.jetbrains.com/help/idea/jetgradle-tool-window.html">Gradle Tool Window</a> by clicking the Elephant Icon on the right hand sidebar.</p>
  </step>
  <step>
  <p>Within this tool window expand `Tasks` and `application`.</p>
  </step>
  <step>
  <p>Double-click on the run task.</p>
  </step>
</procedure>

Your Ktor application should start in a [Run Tool Window](https://www.jetbrains.com/help/idea/run-tool-window.html) at
the bottom of the IDE:

[//]: # (include screenshot)

The same messages that we saw on the command line should appear in the window. Once again you can open your browser at
the specified URL (http://0.0.0.0:8080) and confirm that the project is running.

[//]: # (include screenshot)

You can manage the application via the Run Tool Window. Click on the red box to stop the application and the curved
arrow to restart it. These options are explained further
in [the IntelliJ IDEA Run Tool Window documentation](https://www.jetbrains.com/help/idea/run-tool-window.html#run-toolbar).

[//]: # (Add link for Common problems document)

Note that if you try to start a second copy of the application, whilst the first is already running, then the second
will be unable to run. This is one of several common errors that developers encounter when beginning to write
server-side code. You can find these errors, plus their resolutions, explained in the Common Problems document.

## Additional Tasks To Attempt {id="additional-tasks"}

Here are some additional tasks you may wish to try:

1. [Change the default port.](#change-the-default-port)
2. [Change the port via YAML.](#change-the-port-via-yaml)
3. [Add a new HTTP endpoint.](#add-a-new-http-endpoint)
4. [Configure static content.](#configure-static-content)
5. [Write an integration test.](#write-an-integration-test)
6. [Register error handlers.](#register-error-handlers)

These tasks do not depend on one another, but gradually increase in complexity. Attempting them in the order declared is
the easiest way to learn incrementally. For simplicity, and to avoid duplication, the descriptions below assume you are
attempting the tasks in order.

Where coding is required we have specified both the code and the corresponding imports. The IDE may add these imports
for you automatically.

### Change the default port

In the Project View navigate to the `src/main/kotlin` folder and then into the single package that has been created for
you and follow the steps:

<procedure>
  <step>
    <p> Open the <code>Application.kt</code> file. You should find code similar to the following:</p>
    <code-block lang="kotlin">
    fun main() {
        embeddedServer(
            Netty, 
            port = 8080, // This is the port on which Ktor is listening
            host = "0.0.0.0", 
            module = Application::module
        ).start(wait = true)
    }
    fun Application.module() {
    configureRouting()
      }
    </code-block>
</step>
<step>
  <p>Switch the port number to another of your choosing, such as 9292.</p>
</step>
<step>
  <p>Restart the application.</p>
</step>
<p>You should now find that your application is running under the new port number. You can verify this in the browser, or
by creating a new HTTP Request file in IntelliJ IDEA:</p>
</procedure>

[//]: # (include screenshot)

### Change the port via YAML

When creating a new Ktor project, you have the option to store configuration externally, within either a `YAML` or
a `HOCON` file:

[//]: # (include screenshot)

If you had chosen to store configuration externally then this would be the code in `Application.kt`:

```Kotlin
fun main(args: Array<String>): Unit =
io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    configureRouting()
}
```

These would be the values stored in the configuration file (YAML version):

```yaml
ktor:
    application:
        modules:
            - com.example.ApplicationKt.module
    deployment:
        port: 8080
```

In this case you do not need to alter any code to change the port number. Simply alter the value in the YAML or HOCON
file and restart the application. The change can be verified in the same way as
with [changing the default port](#change-the-default-port) above.

### Add a new HTTP endpoint

Notice the call to `configureRouting()` in the `Application.kt` file. Navigate to this function by using
the [Go-To Declaration](https://www.jetbrains.com/help/idea/navigating-through-the-source-code.html#go_to_declaration)
shortcut or opening the file `Routing.kt` manually. This is the code you should see:

```Kotlin
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

Insert the additional five lines of code shown below to create a new endpoint:

```Kotlin
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Add the five lines below
        get("/test1") {
            val text = "<h1>Hello From Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
    }
}
```

In order to make use of `ContentType` the following import is required:

```Kotlin
import io.ktor.http.*
```

Note that the “/test1” URL can be whatever you like. Restart the application and request the new URL in the browser. The
port number you should use will depend on whether you have attempted the first task. You should see the output displayed
below:

[//]: # (include screenshot)

If you have created an HTTP Request File you can also verify the new endpoint there:

[//]: # (include screenshot)

Note that a line containing three hashes (###) is needed to separate different requests.

### Configure static content

Open the file `Routing.kt`, as in the previous task. Once again this should be the default content:

```Kotlin
fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

Add the following line to the routing section. For this task it does not matter whether you have inserted the
content for the extra endpoint specified in [changing the port via YAML](#change-the-port-via-yaml).

```Kotlin
fun Application.configureRouting() {
    routing {
        // Add the line below
        staticResources("/content", "mycontent")


        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

The following import is required:

```Kotlin
import io.ktor.server.http.content.*
```

The meaning of this line is as follows:

- Invoking staticResources tells Ktor that we want our application to be able to provide standard website content, such
  as HTML and JavaScript files. Although this content may be executed within the browser it is considered static from
  the server's point of view.
- The URL  `/content` specifies the path that should be used to fetch this content.
- The path `mycontent` is the name of the folder within which the static content will live. Ktor will look for this
  folder within the `resources` directory.

<procedure>
  <p>In order to start serving this static content:</p>
  <step>
    <p>Right-click on the <code>src/main/resources</code> folder within the project and create a directory called <code>mycontent</code>.
    Note you could also have used the <a href="https://www.jetbrains.com/help/idea/add-items-to-project.html#create-new-items">new items shortcut</a>.</p>
  </step>
  <step>
    <p>Within this folder right-click (or use the shortcut) and create a web page called <code>sample.html</code>. Populate the page with some sample HTML tags.</p>
  </step>
  <step>
    <p>Restart the application.</p>
  </step>
</procedure>

Your project should now look something like this:

[//]: # (include screenshot)

When you open your browser at http://0.0.0.0:9292/content/sample.html the content of your sample page should be
displayed:

[//]: # (include screenshot)

### Write an integration test

Ktor provides support for creating integration tests, and a skeleton test is created for you
underneath `src/test/kotlin`.
Assuming you have accepted the default settings the class will be called `ApplicationTest` and live in the package
`com.example`.

Open the class and add the code below:

```Kotlin
class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }


        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }
}
```

The following imports are required:

```Kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test
```

The `testApplication()` method creates a new instance of Ktor. This instance is running inside a test environment, as
opposed to a server such as Netty.

You can then use the `application()` method to invoke the same setup that is called from `embeddedServer()`.
Finally, we can use the built-in `client` object and JUnit assertions to send a sample request and check the response.

The test can be run in any of the standard ways for executing tests in IntelliJ IDEA. Note that, because we are running
a new instance of Ktor, the success or failure of the test does not depend on whether your application is running at
0.0.0.0.

If you have successfully completed [changing the port via YAML](#change-the-port-via-yaml) then you should be able to
add this additional test:

```Kotlin
@Test
fun testNewEndpoint() = testApplication {
    application {
        module()
    }


    val response = client.get("/test1")
    assertEquals(HttpStatusCode.OK, response.status)
    assertEquals("html", response.contentType()?.contentSubtype)
    assertContains(response.bodyAsText(), "Hello From Ktor")
}
```

The following additional import is required:

```Kotlin
import kotlin.test.assertContains
```

### Register error handlers

You can handle errors in your Ktor application by using the [Status Pages Plugin](status_pages.md). This plugin is not
included in your project by default. We could have added it to our project via the Plugins section in the Ktor Project
Generator, or the Project Wizard in IntelliJ IDEA. But for educational purposes we will add and configure the plugin
ourselves.

There are four stages to the task:

1. [Add a new dependency in the Gradle build file.](#add-dependency)
2. [Install the plugin and specify an exception handler.](#install-plugin-and-specify-handler)
3. [Write sample code to trigger the handler.](#write-sample-code)
4. [Restart and invoke the sample code.](#restart-and-invoke)

#### Add a new dependency {id="add-dependency"}

In the dependencies section of `build.gradle.kts` add the extra dependency shown below:

```Kotlin
dependencies {
    // Our new dependency to be added
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")


    // The existing dependencies
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
```

<p>When you have done this you will need to reload the project to pick up this new dependency. The shortcut for this is
<shortcut>Shift+Cmd+I</shortcut> on macOS and <shortcut>Ctrl+Shift+O</shortcut> on Windows.
</p>

#### Install the plugin and specify an exception handler {id="install-plugin-and-specify-handler"}

In the `configureRouting()` method in `Routing.kt` add the following lines of code:

```Kotlin
fun Application.configureRouting() {
    //Add the five lines below
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

The following import will be needed:

```Kotlin
import io.ktor.server.plugins.statuspages.*
```

These lines install the Status Pages Plugin and specify what actions to take when an exception of type
`IllegalStateException` is thrown. Note we should be setting an HTTP error code in the response, but we will omit this
so that the output is displayed directly in the browser.

#### Write sample code to trigger the handler {id="write-sample-code"}

Staying within the `configureRouting()` method add the additional lines shown below:

```Kotlin
fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        //Add the three lines below
        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }
    }
}
```

You can see that we have added an endpoint with a URL of `/error-test`. When this endpoint is triggered we will throw an
exception with the type used in our handler.

#### Restart and invoke the sample code {id="restart-and-invoke"}

Restart the application and, in your browser, navigate to the URL [](http://0.0.0.0:9292/error-test). You should see
that the error message is displayed, as shown below.

[//]: # (include screenshot)
