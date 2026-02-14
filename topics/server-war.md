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

You can run a Ktor application inside a servlet container, such as Tomcat or Jetty. To do this, you need to package
your application as a WAR archive and deploy it to a server or a cloud service that supports WAR deployment.

In this topic, you'll learn how to:
* [Configure Ktor for use in a servlet application](#configure-ktor).
* Apply the [Gretty](#configure-gretty) and [War](#configure-war) plugins to run and package WAR applications.
* [Run a Ktor application in a servlet container](#run).
* [Generate and deploy a WAR archive](#generate-war).

## Configure Ktor in a servlet application {id="configure-ktor"}

Ktor allows you to [create and start a server](server-create-and-configure.topic) using a specific engine (such as Netty, Jetty, or Tomcat) directly
in your application. In this setup, your application controls engine configuration, connections, and SSL settings.

When deploying to a servlet container, the container controls the application lifecycle and connection configuration.
For this, Ktor provides the [`ServletApplicationEngine`](https://api.ktor.io/ktor-server-servlet-jakarta/io.ktor.server.servlet.jakarta/-servlet-application-engine/index.html)
engine, which delegates control of your application to the servlet container.

> When running inside a servlet container, Ktor [connection and SSL settings defined in a configuration file](server-configuration-file.topic)
> are not applied.
> 
> For configuring SSL in Tomcat, see the [tomcat-war-ssl](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war-ssl) sample.
> 
{style="note"}

### Add dependencies {id="add-dependencies"}

To use Ktor in a servlet application, add the `ktor-server-servlet-jakarta` artifact to your build script:

<var name="artifact_name" value="ktor-server-servlet-jakarta"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

You do not need to add separate [Jetty or Tomcat engine dependencies](server-engines.md#dependencies) when deploying to
a servlet container.

### Configure a servlet {id="configure-servlet"}

To register a Ktor servlet in your application, open the <path>WEB-INF/web.xml</path> file and assign
`ServletApplicationEngine` to the `servlet-class` attribute:

<tabs>
<tab title="Tomcat/Jetty v10.x+">

```xml
```
{src="snippets/jetty-war/src/main/webapp/WEB-INF/web.xml" include-lines="7-16"}

</tab>
<tab title="Tomcat/Jetty v9.x">
<code-block lang="XML">
<![CDATA[
<servlet>
    <display-name>KtorServlet</display-name>
    <servlet-name>KtorServlet</servlet-name>
    <servlet-class>io.ktor.server.servlet.ServletApplicationEngine</servlet-class>
    <init-param>
        <param-name>io.ktor.ktor.config</param-name>
        <param-value>application.conf</param-value>
    </init-param>
    <async-supported>true</async-supported>
</servlet>
]]>
</code-block>
</tab>
</tabs>

Then, configure the URL pattern for this servlet:

```xml
```
{src="snippets/jetty-war/src/main/webapp/WEB-INF/web.xml" include-lines="18-21"}

## Configure the Gretty plugin {id="configure-gretty"}

The [Gretty](https://plugins.gradle.org/plugin/org.gretty) plugin allows you to [run](#run) a servlet application on Jetty and Tomcat.

To apply the plugin, open your <path>build.gradle.kts</path> file and add the following entry to the
`plugins` block:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="5,8,10"}

Then, you can configure it in the `gretty` block as follows:

<tabs>
<tab title="Jetty">

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="12-15"}

</tab>
<tab title="Tomcat">

```groovy
```
{src="snippets/tomcat-war/build.gradle.kts" include-lines="12-15"}

</tab>
</tabs>

Finally, configure the `run` task:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="31-35"}

## Configure the War plugin {id="configure-war"}

The War plugin allows you to [generate](#generate-war) a WAR archive for deployment to a servlet container.

To apply the plugin, open your <path>build.gradle.kts</path> file and add the following entry to the `plugins` block :

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="5,9-10"}

## Run the application {id="run"}

You can run a servlet application with the [configured Gretty plugin](#configure-gretty) by using the `run` task. For example, to run
the [`jetty-war`](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) sample project, run the following command:

```Bash
./gradlew :jetty-war:run
```

## Generate and deploy a WAR archive {id="generate-war"}

To generate a WAR archive using the [`War`](#configure-war) plugin, run the `war` task. For the [`jetty-war`](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) sample
project, the command looks as follows:

```Bash
./gradlew :jetty-war:war
```

After the task completes, the `jetty-war.war` is available in the <path>build/libs</path> directory of the
corresponding module.

To deploy the generated archive, copy the file to the <path>jetty/webapps</path> directory in your servlet container.

The following `Dockerfile` example shows how to run the generated WAR file inside a Jetty or Tomcat servlet container:

<tabs>
<tab title="Jetty">

```Docker
```
{src="snippets/jetty-war/Dockerfile"}

</tab>
<tab title="Tomcat">

```Docker
```
{src="snippets/tomcat-war/Dockerfile"}

</tab>
</tabs>

For the complete examples, see [jetty-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) and [tomcat-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war).