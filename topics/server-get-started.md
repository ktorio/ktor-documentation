[//]: # (title: Get started with Ktor Server)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="server-get-started"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to open, run and test a server application with Ktor.
</link-summary>

Welcome to the Get Started guide for Ktor Server!

This is the first of a series of tutorials to get you started with
building server applications with Ktor. You can do the tutorials individually,
however, we strongly recommend that you do them in the suggested order:

1. Get started with Ktor Server.
2. [Create HTTP APIs](creating_http_apis.md).
3. [Create a static website](creating_static_website.md).
4. [Create an interactive website](creating_interactive_website.md).
5. [Create a WebSocket chat](creating_web_socket_chat.md).

In this section, you will learn how to create, open and run
your first Ktor server project. Once you get up and running, you can attempt a series of tasks to familiarize yourself
with Ktor.

## Create a new Ktor project {id="create-project"}

One of the fastest ways to create a new Ktor project is by using the
web-based [Ktor project generator](https://start.ktor.io/?_ga=2.179570276.13899987.1704363276-861017825.1700565260&_gl=1*422eap*_ga*ODYxMDE3ODI1LjE3MDA1NjUyNjA.*_ga_9J976DJZ68*MTcwNDM3MTUzOC40OS4xLjE3MDQzNzI5NDMuNTQuMC4w#/settings?name=ktor-sample-app&website=example.com&artifact=com.example.ktor-sample&kotlinVersion=1.9.22&ktorVersion=2.3.7&buildSystem=GRADLE&engine=NETTY&configurationIn=CODE&addSampleCode=true&plugins=).

To create a new project, follow the steps below:

<procedure>
  <step>
   <p>Navigate to the <a href="https://start.ktor.io/?_ga=2.179570276.13899987.1704363276-861017825.1700565260&_gl=1*422eap*_ga*ODYxMDE3ODI1LjE3MDA1NjUyNjA.*_ga_9J976DJZ68*MTcwNDM3MTUzOC40OS4xLjE3MDQzNzI5NDMuNTQuMC4w#/settings?name=ktor-sample-app&website=example.com&artifact=com.example.ktor-sample&kotlinVersion=1.9.22&ktorVersion=2.3.7&buildSystem=GRADLE&engine=NETTY&configurationIn=CODE&addSampleCode=true&plugins=">Ktor Project Generator</a>.</p>
  </step>
  <step>
    <p>In the <b>Project Name</b> field, enter <code>ktor-sample-app</code> as the name of your project.</p>
    <img src="ktor_project_generator_new_project_name.png" alt="Ktor Project Generator with Project Name ktor-sample-app" border-effect="line" width="706"/>
  </step>
  <step>
    <p>Click <b>Add Plugins</b>. For the sake of this tutorial, no plugins need to be added to the project at this stage.</p>
  </step>
  <step>
    <p>On the next screen, click <b>Generate project</b>.</p>
    <img src="ktor_project_generator_new_project_plugins_screen.png" alt="Ktor Project Generator plugins screen" border-effect="line" width="706"/>
  </step>
  <p>Your download should start automatically.</p>
</procedure>


> An alternative way to create a new Ktor project
> is [by using the plugin for IntelliJ IDEA Ultimate](intellij-idea.topic).
> If you choose to use this method instead, make sure to keep the same settings as in the method above and
> leave out any optional plugins.
>
{style="tip"}

## Unpack and run the project {id="unpacking"}

In this section you will learn how to unpack, build and run the project from the command line. The descriptions below
assume that:

- You have created and downloaded a project called `ktor-sample-app`.
- This has been placed in a folder called `myprojects` in your home directory.

If necessary, alter the names and paths to match your own setup.

Open a command line tool of your choice and follow the steps:

<procedure>
  <step>
  <p>In a terminal, navigate to the folder where you downloaded the project:</p>
  <code-block lang="console">
    cd ~/myprojects
  </code-block>
  </step>
 <step>
  <p>Ensure the folder only consists of the ZIP archive:</p>
  <code-block lang="console">
    ls
  </code-block>
  <p>The <code>ls</code> command lists all the files and directories under the specified directory.</p>
  <p>The expected output is:</p>
  <code-block prompt="$ " lang="console">
  ls
  ktor-sample-app.zip
  </code-block>
  </step>
 <step>
  <p>Unpack the ZIP archive into a folder of the same name:</p>
  <tabs>
    <tab title="macOS" group-key="macOS">
    <code-block lang="console">
      unzip ktor-sample-app.zip  -d ktor-sample-app
    </code-block>
    </tab>
    <tab title="Windows" group-key="windows">
      <code-block lang="console">
        tar -xf ktor-sample-app.zip
      </code-block>
    </tab>
  </tabs>
  <p>Your directory will now contain the ZIP archive and the unpacked folder.</p>
  </step>
  <step>
    <p>From the directory, navigate into the newly created folder:</p>
    <code-block lang="console">
        cd ktor-sample-app
    </code-block>
  </step>
  <step>
    <p>Use the <code>chmod</code> command to make the gradlew Gradle helper script executable:</p>
   <code-block lang="console">
    chmod +x ./gradlew
  </code-block>
  </step>
  <step>
    <p>You can verify that the file is executable by examining the first word of output from <code>ls -l</code>.</p>
   <code-block lang="console">
    ls -l gradlew | cut -d " " -f 1
  </code-block>
    <p>The 4th, 7th and 10th characters in the returned string should be an ‘x':</p>
    <code-block prompt="$ " lang="console">
    ls -l gradlew | cut -d " " -f 1
    -rwxr-xr-x@
    </code-block>
    <p>Now that the <code>gradlew</code> script is executable, you can build the project via the Gradle build tool.</p>
  </step>
  <step>
    <p>To build the project, use the following command:</p>
   <code-block lang="console">
    ./gradlew build
  </code-block>
    <p>If you see that your build has been successful you can execute the project, again via Gradle.</p>
  </step>
  <step>
    <p>To run the project, use the following command:</p>
   <code-block lang="console">
    ./gradlew run
  </code-block>
  </step>
  <step>
    <p>To verify the project is running, open a browser at the URL mentioned in the output (<a href="http://0.0.0.0:8080">http://0.0.0.0:8080</a>).
    You should see the message "Hello World!" displayed on the screen:</p>
    <img src="server_get_started_ktor_sample_app_output.png" alt="Output of generated ktor project" border-effect="line" width="706"/>
  </step>
  </procedure>

Congratulations! You have successfully started your Ktor project.

<p>Note that the command line is unresponsive because the underlying process is busy running the Ktor application. You can
press <shortcut>CTRL+C</shortcut> to terminate the application.</p>

## Open, explore and run in IntelliJ IDEA {id="open-explore-run"}

### Open the project {id="open"}

If you have [IntelliJ IDEA](https://www.jetbrains.com/idea/) installed, you can easily open the project from the command
line.

Make sure you are in the project folder and then type the `idea` command, followed by a period to represent the current
folder:

```Bash
idea .
```

Alternatively, to open the project manually launch IntelliJ IDEA.

If the Welcome screen opens, click **Open**. Otherwise, go to **File | Open** in the main menu and select
the `ktor-sample-app` folder to open it.

> For more details on managing projects,
> see [the IntelliJ IDEA documentation](https://www.jetbrains.com/help/idea/creating-and-managing-projects.html).
>
{style="tip"}

### Explore the project {id="explore"}

Whichever option you choose, the project should open as shown below:

![Generated Ktor project view in IDE](server_get_started_idea_project_view.png){ width="706" }

In order to explain the project layout we have expanded the structure in the **Project** view and selected the
file `settings-gradle.kts`.

You will see that the code to run your application lives in packages under `src/main/kotlin`. The default package is
called `com.example` and contains a subpackage called `plugins`.
Two files have been created within these packages, named `Application.kt` and `Routing.kt`.

![Ktor project src folder structure](server_get_started_idea_main_folder.png){ width="400" }

The name of the project is configured in `settings-gradle.kts`.

![Contents of settings.gradle.kt](server_get_started_idea_settings_file.png){ width="706" }

Configuration files, and other kinds of content, live within the `src/main/resources` folder.

![Ktor project resources folder structure](server_get_started_idea_resources_folder.png){ width="400" }

A skeleton test has been created in a package under `src/test/kotlin`.

![Ktor project test folder structure](server_get_started_idea_test_folder.png){ width="400" }

### Run the project {id="run"}

<procedure>
  <p>To run the project from within IntelliJ IDEA:</p>
  <step>
    <p>Open the <a href="https://www.jetbrains.com/help/idea/jetgradle-tool-window.html">Gradle tool window</a>
      by clicking the Gradle icon (<img alt="intelliJ IDEA gradle icon" src="intellij_idea_gradle_icon.svg" width="16" height="26"/>) on the right sidebar.</p>
    <img src="server_get_started_idea_gradle_tab.png" alt="Gradle tab in IntelliJ IDEA" border-effect="line" width="706"/>
  </step>
  <step>
    <p>Within this tool window navigate to <control>Tasks | application</control> and double-click on the <b>run</b> task.</p>
    <img src="server_get_started_idea_gradle_run.png" alt="Gradle tab in IntelliJ IDEA" border-effect="line" width="450"/>
  </step>
  <step>
    <p>Your Ktor application will start in the <a href="https://www.jetbrains.com/help/idea/run-tool-window.html">Run tool window</a> at
    the bottom of the IDE:</p>
    <img src="server_get_started_idea_run_terminal.png" alt="Project running in terminal" width="706" />
    <p>The same messages that were previously displayed on the command line will now be visible in the <b>Run</b> tool window.</p>
  </step>
  <step>
    <p>To confirm the project is running, open your browser at the specified URL
    (<a href="http://0.0.0.0:8080">http://0.0.0.0:8080</a>).</p>
    <p>You should once again see the message "Hello World!" displayed on the screen:</p>
    <img src="server_get_started_ktor_sample_app_output.png" alt="Hello World in Browser Screen" width="706" />
  </step>
</procedure>

You can manage the application via the **Run** tool window.

- To terminate the application, click the stop button (![](intellij_idea_terminate_icon.svg){style="inline" height="16"
  width="16"}).
- To restart the process, click the rerun button (![](intellij_idea_rerun_icon.svg){style="inline" height="16"
  width="16"}).

These options are explained further
in [the IntelliJ IDEA Run Tool Window documentation](https://www.jetbrains.com/help/idea/run-tool-window.html#run-toolbar).

> Note that if you try to start a second copy of the application, whilst the first is already running, then the second
> will be unable to run. This is one of several common errors that developers encounter when beginning to write
> server-side code. You can find these errors, plus their resolutions, explained in the [Common Problems document]().
>
{style="tip"}

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

In the **Project** view navigate to the `src/main/kotlin` folder and then into the single package that has been created
for you and follow the steps:

<procedure>
  <step>
    <p>Open the <code>Application.kt</code> file. You should find code similar to the following:</p>
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
  <p>In the <code>embeddedServer()</code> function, change the <code>port</code> parameter 
  to another number of your choosing, such as "9292".</p>
    <code-block lang="kotlin">
    fun main() {
        embeddedServer(
            Netty, 
            port = 9292,
            host = "0.0.0.0", 
            module = Application::module
        ).start(wait = true)
    }
    </code-block>
</step>
<step>
  <p>Click on the rerun button (<img alt="intelliJ IDEA rerun button icon" src="intellij_idea_rerun_icon.svg" height="16" width="16" />)
    to restart the application.</p>
</step>
<step>
  <p>To verify that your application is running under the new port number, you can open your browser at the new URL (<a href="http://0.0.0.0:9292">http://0.0.0.0:9292</a>), or
   <a href="https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#creating-http-request-files">create a new HTTP Request file in IntelliJ IDEA</a>:</p>
  <img src="server_get_started_port_change.png" alt="Testing port change with an HTTP request file in IntelliJ IDEA" width="706"/>
</step>
</procedure>

### Change the port via YAML

When creating a new Ktor project, you have the option to store configuration externally, within either a YAML or
a HOCON file:

![Ktor project generator configuration options](ktor_project_generator_configuration_options.png){ width="400" }

If you had chosen to store configuration externally then this would be the code in `Application.kt`:

```Kotlin
fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    configureRouting()
}
```

These would be the values stored in the configuration file within `src/main/resources/`:

<tabs>
  <tab title="application.yaml (YAML)" group-key="yaml">
    <code-block lang="yaml">
    ktor:
        application:
            modules:
                - com.example.ApplicationKt.module
        deployment:
            port: 8080
    </code-block>
  </tab>
  <tab title="application.conf (HOCON)" group-key="hocon">
    <code-block lang="json">
      ktor {
    deployment {
        port = 8080
        port = ${?PORT}
    }
    application {
            modules = [ com.example.ApplicationKt.module ]
        }
    }
    </code-block>
  </tab>
</tabs>

In this case you do not need to alter any code to change the port number. Simply alter the value in the YAML or HOCON
file and restart the application. The change can be verified in the same way as
with [changing the default port](#change-the-default-port) above.

### Add a new HTTP endpoint

Next, you will create a new HTTP endpoint that will respond to a GET request.

In the **Project** tool window, navigate to the `src/main/kotlin/com/example` folder and follow the steps:

<procedure>
  <step>
    <p>Open the <code>Application.kt</code> file and find the <code>configureRouting()</code> function.</p>
  </step>
  <step>
    <p>In intelliJ IDEA, navigate to the <code>configureRouting()</code> function by placing the caret over the function name 
        and pressing <shortcut layout="macOS">⌘Cmd+C</shortcut>.</p>
    <p>Alternatively, you can navigate to the function by opening the <code>Routing.kt</code> file.</p>
    <p>This is the code you should see:</p>
    <code-block lang="kotlin">
    fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
            }
        }
    }
    </code-block>
  </step>
  <step>
    <p>To create a new endpoint, insert the additional five lines of code shown below:</p>
    <code-block lang="kotlin"
                src="snippets/server-get-started/src/main/kotlin/com/example/plugins/Routing.kt"
                include-lines="11,17,20-28,32-33"/>
    <p>Note that you can change the <code>/test1</code> URL to be whatever you like.</p>
  </step>
  <step>
    <p>In order to make use of <code>ContentType</code>, add the following import:</p>
    <code-block lang="kotlin">
    import io.ktor.http.*
    </code-block>
  </step>
  <step>
    <p>Click on the rerun button (<img alt="intelliJ IDEA rerun button icon" src="intellij_idea_rerun_icon.svg" height="16" width="16" />)
    to restart the application.</p>
  </step>
  <step>
    <p>Request the new URL in the browser (<a href="http://0.0.0.0:9292/test1">http://0.0.0.0:9292/test1</a>). The
      port number you should use will depend on whether you have attempted the first task (<a href="#change-the-default-port">Changing the default port</a>). You should see the output displayed
      below:</p>
    <img src="server_get_started_add_new_http_endpoint_output.png" alt="A browser screen displaying Hello from Ktor" width="706"/>
    <p>If you have created an HTTP Request File you can also verify the new endpoint there:</p>
    <img src="server_get_started_add_new_http_endpoint.png" alt="An HTTP request file in intelliJ IDEA" width="450"/>
    <p>Note that a line containing three hashes (###) is needed to separate different requests.</p>
  </step>
</procedure>

### Configure static content

In the **Project** tool window, navigate to the `src/main/kotlin/com/example/plugins` folder and follow the steps:

<procedure>
  <step>
    <p>Open the <code>Routing.kt</code> file.</p>
    <p>Once again this should be the default content:</p>
    <code-block lang="kotlin">
    fun Application.configureRouting() {
        routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
    </code-block>
    <p>For this task it does not matter whether you have inserted the
    content for the extra endpoint specified in <a href="#add-a-new-http-endpoint">Adding a new HTTP endpoint</a>.</p>
  </step>
  <step>
    <p>Add the following line to the routing section:</p>
    <code-block lang="kotlin">
    fun Application.configureRouting() {
        routing {
            // Add the line below
            staticResources("/content", "mycontent")
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }
    </code-block>
    <p>The meaning of this line is as follows:</p>
    <list type="bullet">
        <li>Invoking <code>staticResources()</code> tells Ktor that we want our application to be able to provide standard website content, such
        as HTML and JavaScript files. Although this content may be executed within the browser, it is considered static from
        the server's point of view.</li>
        <li>The URL  <code>/content</code> specifies the path that should be used to fetch this content.</li>
        <li>The path <code>mycontent</code> is the name of the folder within which the static content will live. Ktor will look for this
        folder within the <code>resources</code> directory.</li>
    </list>
  </step>
  <step>
    <p>Add the following import:</p>
    <code-block lang="kotlin">
    import io.ktor.server.http.content.*
    </code-block>
  </step>
  <step>
    <p>In the <control>Project</control> tool window, right-click the <code>src/main/resources</code> folder and select <control>New | Directory</control>.</p>
    <p>Alternatively, select the <code>src/main/resources</code> folder, press <shortcut layout="macOS">⌘Сmd+N</shortcut>, and click <control>Directory</control>.</p>
  </step>
  <step>
    <p>Name the new directory <code>mycontent</code> and press <shortcut>↩Enter</shortcut>.</p>
  </step>
  <step>
    <p>Right-click on the newly created folder and click <control>New | File</control>.</p>
  </step>
  <step>
    <p>Name the new file "sample.html" and press <shortcut>↩Enter</shortcut>.</p>
  </step>
  <step>
    <p>Populate the newly created file page with valid HTML, for example:</p>
    <code-block lang="html"
                src="snippets/server-get-started/src/main/resources/mycontent/sample.html"
                include-lines="1-14"/>
  </step>
  <step>
    <p>Click on the rerun button (<img alt="intelliJ IDEA rerun button icon" src="intellij_idea_rerun_icon.svg" height="16" width="16" />)
    to restart the application.</p>
  </step>
  <step>
    <p>When you open your browser at <a href="http://0.0.0.0:9292/content/sample.html">http://0.0.0.0:9292/content/sample.html</a>
    the content of your sample page should be displayed:</p>
    <img src="server_get_started_configure_static_content_output.png" alt="Output of a static page in browser" width="706"/>
  </step>
</procedure>

### Write an integration test

Ktor provides support for creating integration tests, and a skeleton test is created for you
underneath `src/test/kotlin`.

Assuming you have accepted the default settings the class will be called `ApplicationTest` and will live in the
package `com.example`.

In the **Project** tool window, navigate to the `src/test/kotlin` folder and follow the steps:

<procedure>
    <step>
        <p>Open the <code>ApplicationTest.kt</code> file and add the code below:</p>
        <code-block lang="kotlin">
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
        </code-block>
        <p>The <code>testApplication()</code> method creates a new instance of Ktor. This instance is running inside a test environment, as
        opposed to a server such as Netty.</p>
        <p>You can then use the <code>application()</code> method to invoke the same setup that is called from
        <code>embeddedServer()</code>.</p>
        <p>Finally, you can use the built-in <code>client</code> object and JUnit assertions to send a sample request and check the response.</p>
    </step>
    <step>
        <p>Add the following required imports:</p>
        <code-block lang="kotlin">
        import io.ktor.client.request.*
        import io.ktor.client.statement.*
        import io.ktor.http.*
        import io.ktor.server.testing.*
        import org.junit.Assert.assertEquals
        import org.junit.Test
        </code-block>
    </step>
</procedure>

The test can be run in any of the standard ways for executing tests in IntelliJ IDEA. Note that, because you are running
a new instance of Ktor, the success or failure of the test does not depend on whether your application is running at
0.0.0.0.

If you have successfully completed [adding a new HTTP endpoint](#add-a-new-http-endpoint), you should be able to
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

You can handle errors in your Ktor application by using the [StatusPages plugin](status_pages.md).

This plugin is not included in your project by default. You could have added it to your project via the **Plugins**
section in the Ktor
Project Generator, or the Project Wizard in IntelliJ IDEA. Since you've already created your project, in the next steps
you will learn how to add and configure the plugin manually.

There are four steps to achieving this:

1. [Add a new dependency in the Gradle build file.](#add-dependency)
2. [Install the plugin and specify an exception handler.](#install-plugin-and-specify-handler)
3. [Write sample code to trigger the handler.](#write-sample-code)
4. [Restart and invoke the sample code.](#restart-and-invoke)

<procedure title="Add a new dependency" id="add-dependency">
  <p>In the <control>Project</control> tool window, navigate to the <code>src/test/kotlin</code> folder and follow the steps:</p>
    <step>
        <p>Open the <code>build.gradle.kts</code> file.</p>
    </step>
    <step>
        <p>In the dependencies section add the extra dependency as shown below:</p>
        <code-block lang="kotlin">
        dependencies {
                // The new dependency to be added
                implementation("io.ktor:ktor-server-status-pages:$ktor_version")
                // The existing dependencies
                implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
                implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
                implementation("ch.qos.logback:logback-classic:$logback_version")
                testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
                testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
            }
        </code-block>
        <p>When you have done this you will need to reload the project to pick up this new dependency.</p>
    </step>
    <step>
        <p>Reload the project by pressing
        <shortcut>Shift+⌘Cmd+I</shortcut> on macOS or <shortcut>Ctrl+Shift+O</shortcut> on Windows.
        </p>
    </step>
</procedure>

<procedure title="Install the plugin and specify an exception handler" id="install-plugin-and-specify-handler">
    <step>
        <p>Navigate to the <code>configureRouting()</code> method in <code>Routing.kt</code> and add the following lines of code:</p>
        <code-block lang="kotlin"
                    src="snippets/server-get-started/src/main/kotlin/com/example/plugins/Routing.kt"
                    include-lines="11-17,20-22,32-33"/>
        <p>These lines install the <code>StatusPages</code> plugin and specify what actions to take
        when an exception of type <code>IllegalStateException</code> is thrown.</p>
    </step>
    <step>
        <p>Add the following import:</p>
        <code-block lang="kotlin">
            import io.ktor.server.plugins.statuspages.*
        </code-block>
    </step>
</procedure>

Note that an HTTP error code would usually be set in the response, but for the
purpose of this task, the output is displayed directly in the browser.

<procedure title="Write sample code to trigger the handler" id="write-sample-code">
    <step>
        <p>Staying within the <code>configureRouting()</code> method, add the additional lines as shown below:</p>
        <code-block lang="kotlin"
                    src="snippets/server-get-started/src/main/kotlin/com/example/plugins/Routing.kt"
                    include-lines="11-17,20-22,29-33"/>
        <p>You have now added an endpoint with the URL <code>/error-test</code>. When this endpoint is triggered, an
        exception will be thrown with the type used in the handler.</p>
    </step>
</procedure>

<procedure title="Restart and invoke the sample code" id="restart-and-invoke">
    <step>
      <p>Click on the rerun button (<img alt="intelliJ IDEA rerun button icon" src="intellij_idea_rerun_icon.svg" height="16" width="16" />)
      to restart the application.</p>    </step>
    <step>
        <p>In your browser, navigate to the URL <a href="http://0.0.0.0:9292/error-test">http://0.0.0.0:9292/error-test</a>.
        You should see the error message displayed as shown below:</p>
        <img src="server_get_started_register_error_handler_output.png" 
        alt="A browser screen with message `App in illegal state as Too Busy`" width="706"/>
    </step>
</procedure>

## What's next

If you've made it to the end of the additional tasks, you now have a grasp of configuring the Ktor
server, integrating a Ktor plugin, and implementing a new route. However, this is just the beginning. To delve deeper
into the foundational concepts of Ktor, continue to the next tutorials in this guide.

In the next tutorial, you will learn about routing a requests by [creating an HTTP API](creating_http_apis.md).