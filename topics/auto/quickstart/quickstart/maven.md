[//]: # (title: Maven)
[//]: # (caption: Setting up a Maven Build)
[//]: # (category: quickstart)
[//]: # (toc: true)
[//]: # (permalink: /quickstart/quickstart/maven.html)
[//]: # (redirect_from: redirect_from)
[//]: # (- /quickstart/maven.html: - /quickstart/maven.html)

In this guide, we will show you how to create a Maven `pom.xml` file
and how to configure it to support Ktor.





## Basic Kotlin `pom.xml` file (without Ktor)
{id="initial"}

Maven is a build automation tool used primarily for Java projects.
It reads project configuration from `pom.xml` files.
Here is a basic `pom.xml` file for building Kotlin applications:


```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>org.jetbrains</groupId>
    <artifactId>sample</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>org.jetbrains sample</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <kotlin.version>{{site.kotlin_version}}</kotlin.version>
        <junit.version>4.12</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-test-junit</artifactId>
            <version>${kotlin.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <sourceDirectory>src/main/kotlin</sourceDirectory>
        <testSourceDirectory>src/test/kotlin</testSourceDirectory>

        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>test-compile</id>
                        <phase>test-compile</phase>
                        <goals>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## Add Ktor dependencies and configure build settings
{id="ktor-dependencies"}

Ktor artifacts are located in a specific repository on bintray.
And its core has dependencies on the `kotlinx.coroutines` library that
can be found on `jcenter`.

You have to add both to the `repositories` block in the `pom.xml` file:
   
```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com</url>
    </repository>
</repositories>
``` 

Visit [Bintray](https://bintray.com/kotlin/ktor/ktor) and determine the latest version of ktor.
In this case it is `%ktor_version%`.

You have to specify that version in each Ktor artifact reference,
and to avoid repetitions, you can specify that version in an extra property
in the `properties` block for using it later:

```xml
<properties>
    <ktor.version>{{site.ktor_version}}</ktor.version>
</properties>
```

Now you have to add `ktor-server-core` artifact using the `ktor.version` you specified:
 
```xml
<dependency>
    <groupId>io.ktor</groupId>
    <artifactId>ktor-server-core</artifactId>
    <version>${ktor.version}</version>
</dependency>
```

## Choose your engine and configure it
{id="engine"}

Ktor can run in many environments, such as Netty, Jetty or any other
Servlet-compatible Application Container such as Tomcat.

This example shows how to configure Ktor with Netty.
For other engines see [artifacts](/quickstart/artifacts.html) for a list of
available artifacts.

You will add a dependency for `ktor-server-netty` using the
`ktor.version` property you have created. This module provides
Netty as a web server and all the required code to run Ktor
application on top of it:

```xml
<dependency>
    <groupId>io.ktor</groupId>
    <artifactId>ktor-server-netty</artifactId>
    <version>${ktor.version}</version>
</dependency>
```

## Final `pom.xml` (with Ktor)
{id="complete"}

When you are done, the `pom.xml` file should look like:


```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>org.jetbrains</groupId>
    <artifactId>sample</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>org.jetbrains sample</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <kotlin.version>{{site.kotlin_version}}</kotlin.version>
        <ktor.version>{{site.ktor_version}}</ktor.version>
        <junit.version>4.12</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-test-junit</artifactId>
            <version>${kotlin.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-server-netty</artifactId>
            <version>${ktor.version}</version>
        </dependency>

    </dependencies>

    <build>
        <sourceDirectory>src/main/kotlin</sourceDirectory>
        <testSourceDirectory>src/test/kotlin</testSourceDirectory>

        <plugins>
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>test-compile</id>
                        <phase>test-compile</phase>
                        <goals>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <jvmTarget>1.8</jvmTarget>
                    <args>
                        <arg>-Xcoroutines=enable</arg>
                    </args>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>jcenter</id>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>
</project>
```

You can now run `mvn package` to fetch dependencies and verify everything is set up correctly.

## Configure logging
{id="logging"}

If you want to log application events and useful information,
you can read further in the [logging](/servers/logging.html) page.