[//]: # (title: WAR)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<b>Code examples</b>: 
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war">jetty-war</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war">tomcat-war</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war-ssl">tomcat-war-ssl</a>
</p>
</tldr>

<link-summary>
Learn how to run and deploy a Ktor application inside a servlet container using a WAR archive.
</link-summary>

A Ktor application can be run and deployed inside servlet containers that include Tomcat and Jetty. To deploy inside a servlet container, you need to generate a WAR archive and then deploy it to a server or a cloud service that supports WARs.

> Ktor supports Jetty up to the 9.4.x version and Tomcat up to 9.0.x.

In this topic, we'll show you how to:
* configure Ktor to use it in a servlet application;
* apply Gretty and War plugins for running and packaging WAR applications;
* run a Ktor servlet application;
* generate and deploy a WAR archive.



## Configure Ktor in a servlet application {id="configure-ktor"}

Ktor allows you to [create and start a server](create_server.topic) with the desired engine (such as Netty, Jetty, or Tomcat) right in the application. In this case, your application has control over engine settings, connection, and SSL options.

In contrast to the approach above, a servlet container should control the application lifecycle and connection settings. Ktor provides a special [ServletApplicationEngine](https://api.ktor.io/ktor-server/ktor-server-servlet/io.ktor.server.servlet/-servlet-application-engine/index.html) engine that delegates control over your application to a servlet container.

> Note that [connection and SSL settings](Configurations.topic#configuration-file) are not in effect when a Ktor application is deployed inside a servlet container. 
> The [tomcat-war-ssl](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war-ssl) sample demonstrates how to configure SSL in Tomcat.



### Add dependencies {id="add-dependencies"}

To use Ktor in a servlet application, you need to include the `ktor-server-servlet` artifact in the build script:
<var name="artifact_name" value="ktor-server-servlet"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

Note that you don't need the separate [Jetty or Tomcat artifacts](Engines.md#dependencies) when a Ktor application is deployed inside a servlet container.

### Configure a servlet {id="configure-servlet"}

To register a Ktor servlet in your application, open the `WEB-INF/web.xml` file and assign [ServletApplicationEngine](https://api.ktor.io/ktor-server/ktor-server-servlet/io.ktor.server.servlet/-servlet-application-engine/index.html) to the `servlet-class` attribute:

```xml
```
{src="snippets/jetty-war/src/main/webapp/WEB-INF/web.xml" lines="7-16"}

Then, configure the URL pattern for this servlet:

```xml
```
{src="snippets/jetty-war/src/main/webapp/WEB-INF/web.xml" lines="18-21"}



## Configure Gretty {id="configure-gretty"}

The [Gretty](https://plugins.gradle.org/plugin/org.gretty) plugin allows you to [run](#run) a servlet application on Jetty and Tomcat. To install this plugin, open the `build.gradle.kts` file and add the following code to the `plugins` block:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" lines="5,8,10"}

Then, you can configure it in a `gretty` block as follows:

<tabs>
<tab title="Jetty">

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" lines="12-15"}

</tab>
<tab title="Tomcat">

```groovy
```
{src="snippets/tomcat-war/build.gradle.kts" lines="12-16"}

</tab>
</tabs>

Note that if you want to use Tomcat, you need to specify `servletContainer` explicitly.

Finally, configure the `run` task:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" lines="29-33"}



## Configure War {id="configure-war"}

The War plugin allows you to [generate](#generate-war) WAR archives. You can install it by adding the following line to the `plugins` block in your `build.gradle.kts` file:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" lines="5,9-10"}




## Run an application {id="run"}

You can run a servlet application with the [configured Gretty plugin](#configure-gretty) by using the `run` task. For example, the following command runs the [jetty-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) example:

```Bash
./gradlew :jetty-war:run
```

## Generate and deploy a WAR archive {id="generate-war"}

To generate a WAR file with your application using the [War](#configure-war) plugin, execute the `war` task. For the [jetty-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) example, a command looks as follows:

```Bash
./gradlew :jetty-war:war
```

The `jetty-war.war` is created in the `build/libs` directory. You can deploy the generated archive inside a servlet container by copying it to the `jetty/webapps` directory. For instance, a `Dockerfile` below shows how to run the created WAR inside a Jetty or Tomcat servlet container:

<tabs>
<tab title="Jetty">

```dockerfile
```
{src="snippets/jetty-war/Dockerfile"}

</tab>
<tab title="Tomcat">

```dockerfile
```
{src="snippets/tomcat-war/Dockerfile"}

</tab>
</tabs>

You can find the complete examples here: [jetty-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) and [tomcat-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war).
