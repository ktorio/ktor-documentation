[//]: # (title: Logging)
[//]: # (caption: Logging in Ktor)
[//]: # (category: servers)
[//]: # (permalink: /servers/logging.html)
[//]: # (keywords: SLF4J logback log4j)

Ktor uses [SLF4J](https://www.slf4j.org/) for logging.

## SLF4J Providers
{ #providers }

If you don't add a logging provider, you will see the
following message when you run your application:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

We can set up logging to remove these warning messages and get
a better idea of what is happening with the app by adding a provider.

Providers use Java's [ServiceLoader](https://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) mechanism,
and so are discovered and added automatically without having to do anything
else by code.
{ .note.tip }

### Logback provider
{ #providers-logback }

You can use [logback](https://logback.qos.ch/),
which is the successor of log4j, as a SLF4J provider:

Gradle's `build.gradle` or `build.gradle.kts`:
```groovy
compile("ch.qos.logback:logback-classic:1.2.3")
```

Mavens's `pom.xml`:
```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
</dependency>
```

Once added, run the app, and you should now see the logging messages
in the Run pane of IDEA. However, these logging messages are not as
helpful as they could be.

### Configuring the Logback provider
{ #providers-logback-config }

If the default logging is not enough, you can put a `logback.xml` or `logback-test.xml` (that has higher priority) file in your `src/main/resources` folder
to adjust the logging if it is not useful to you. For example:

{% capture logback-xml %}
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="trace">
        <appender-ref ref="STDOUT"/>
    </root>

    <logger name="org.eclipse.jetty" level="INFO"/>
    <logger name="io.netty" level="INFO"/>
</configuration>
```
{% endcapture %}

{% include tabbed-code.html
    tab1-title="logback.xml" tab1-content=logback-xml
    no-height="true"
%}

After it is added, if you stop your app, and run it again, after going
to localhost:8080 in your browser, 
you should see a log message now in the IDEA run pane, something like:

```
2017-05-29 23:08:12.926 [nettyCallPool-4-1] TRACE ktor.application - 200 OK: GET - /
```

You can install the [Call Logging](/servers/features/call-logging.html) feature to catch and log requests.
{ .note}

To understand how to change the `logback.xml` configuration file
and change the logging, see the [logback manual](https://logback.qos.ch/manual/index.html).

## Accessing the main logger
{ #main-logger }

The `ApplicationEnvironment` interface has a `log` property.
You can access it inside an `ApplicationCall` with `call.application.environment.log`.