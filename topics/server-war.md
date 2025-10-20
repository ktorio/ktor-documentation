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

In this topic, we'll show you how to:
* configure Ktor to use it in a servlet application;
* apply Gretty and War plugins for running and packaging WAR applications;
* run a Ktor servlet application;
* generate and deploy a WAR archive.



## Configure Ktor in a servlet application {id="configure-ktor"}

Ktor allows you to [create and start a server](server-create-and-configure.topic) with the desired engine (such as Netty, Jetty, or Tomcat) right in the application. In this case, your application has control over engine settings, connection, and SSL options.

In contrast to the approach above, a servlet container should control the application lifecycle and connection settings. Ktor provides a special [ServletApplicationEngine](https://api.ktor.io/ktor-server-servlet/io.ktor.server.servlet/-servlet-application-engine/index.html) engine that delegates control over your application to a servlet container.

> Note that [connection and SSL settings](server-configuration-file.topic) are not in effect when a Ktor application is deployed inside a servlet container. 
> The [tomcat-war-ssl](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war-ssl) sample demonstrates how to configure SSL in Tomcat.



### Add dependencies {id="add-dependencies"}

To use Ktor in a servlet application, you need to include the `ktor-server-servlet-jakarta` artifact in the build script:

<var name="artifact_name" value="ktor-server-servlet-jakarta"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

If you use the 9.x or earlier version of Tomcat/Jetty, add the `ktor-server-servlet` artifact instead.

> Note that you don't need the separate [Jetty or Tomcat artifacts](server-engines.md#dependencies) when a Ktor application is deployed inside a servlet container.

### Configure a servlet {id="configure-servlet"}

To register a Ktor servlet in your application, open the `WEB-INF/web.xml` file and assign `ServletApplicationEngine` to the `servlet-class` attribute:


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



## Configure Gretty {id="configure-gretty"}

> Ktor 3.3.0 requires Jetty 12, which is not yet supported by Gretty. If you rely on Gretty for development or
> deployment, use Ktor 3.2.3 instead until Gretty adds Jetty 12 support.
>
{style="warning"}

The [Gretty](https://plugins.gradle.org/plugin/org.gretty) plugin allows you to [run](#run) a servlet application on Jetty and Tomcat. To install this plugin, open the `build.gradle.kts` file and add the following code to the `plugins` block:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="5,8,10"}

Then, you can configure it in the `gretty` block as follows:

<tabs>
<tab title="Jetty">

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="12-16"}

</tab>
<tab title="Tomcat">

```groovy
```
{src="snippets/tomcat-war/build.gradle.kts" include-lines="12-16"}

</tab>
</tabs>

Finally, configure the `run` task:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="32-36"}



## Configure War {id="configure-war"}

The War plugin allows you to [generate](#generate-war) WAR archives. You can install it by adding the following line to the `plugins` block in your `build.gradle.kts` file:

```groovy
```
{src="snippets/jetty-war/build.gradle.kts" include-lines="5,9-10"}




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

You can find the complete examples here: [jetty-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/jetty-war) and [tomcat-war](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tomcat-war).
