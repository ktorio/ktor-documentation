[//]: # (title: HTTP/2)
[//]: # (caption: Configure HTTP/2 in Different Application Engines)
[//]: # (category: advanced)
[//]: # (permalink: /advanced/http2.html)
[//]: # (ktor_version_review: 1.0.0)

[HTTP/2](https://en.wikipedia.org/wiki/HTTP/2) is a modern binary duplex multiplexing protocol designed as a replacement for HTTP/1.x.

Jetty, Netty, and Tomcat engines provide HTTP/2 implementations that Ktor can use. However, there are significant differences,
and each engine requires additional configuration. Once your host is configured properly for Ktor, HTTP/2 support will be activated automatically.

Key requirements:

* SSL certificate (can be self-signed)
* ALPN implementation suitable for a particular engine (see corresponding sections for Netty, Jetty, and Tomcat)
* HTTP/2 compliant browsers (all major browsers have supported it since the end of 2015 according to [caniuse.com](http://caniuse.com/#search=http2))

## SSL certificate

As per the specification, HTTP/2 does not require encryption, but all browsers will require encrypted connections to be used with HTTP/2.
That's why a working TLS environment is a prerequisite for enabling HTTP/2. Therefore, a certificate is required to enable encryption.
For testing purposes, it can be generated with `keytool` from the JDK:

```bash
keytool -keystore test.jks -genkeypair -alias testkey -keyalg RSA -keysize 4096 -validity 5000 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```

The next step is configuring Ktor to use your keystore. See the example application.conf:


```kotlin
ktor {
    deployment {
        port = 8080
        sslPort = 8443
        watch = [ ]
    }

    application {
        modules = [ com.example.ModuleKt.main ]
    }

    security {
        ssl {
            keyStore = /path/to/test.jks
            keyAlias = testkey
            keyStorePassword = changeit
            privateKeyPassword = changeit
        }
    }
}
```

## ALPN implementation

HTTP/2 requires ALPN ([Application-Layer Protocol Negotiation](https://en.wikipedia.org/wiki/Application-Layer_Protocol_Negotiation)) to be enabled.
Unfortunately, the JDK's TLS implementation doesn't have support for ALPN, so your application engine must be configured properly. 
The first option is to use an external ALPN implementation that needs to be added to the boot classpath.
Another option is to use OpenSSL native bindings and precompiled native binaries. Both approaches are error-prone and require extra attention when being configured.
Also, each particular engine can support only one of these methods.

### Jetty

Jetty supports the JDK ALPN extension, and to get it working you have to add an extra-dependency to the java *boot classpath*.
It is very important to add it to the *boot* classpath, as adding it to a regular classpath doesn't work.

The other issue is that the exact dependency version depends on the JDK version. For example, for JDK 8u144, alpn boot 8.1.11.v20170118
should be used (see <https://www.eclipse.org/jetty/documentation/9.4.x/alpn-chapter.html#alpn-versions> for the full compatibility list).

The following JVM options should be applied (the path can be relative):

```
-Xbootclasspath/p:/path/to/alpn-boot-8.1.11.v20170118.jar
```

Depending on your build system you will probably need to copy the dependency to some specific directory. 
In Maven you could use `maven-dependency-plugin` (goal `copy-dependencies`) or `Copy` task in Gradle.

```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>unpack-jetty-alpn</id>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <phase>compile</phase>
                    <configuration>
                        <includeArtifactIds>alpn-boot</includeArtifactIds>
                        <stripVersion>true</stripVersion>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

If all of the above is done properly, Jetty will log that ssl, alpn, and h2 are enabled:

```
INFO  org.eclipse.jetty.server.Server - jetty-9.4.6.v20170531
INFO  o.e.jetty.server.AbstractConnector - Started ServerConnector@337762cd{HTTP/1.1,[http/1.1, h2c]}{0.0.0.0:8080}
INFO  o.e.jetty.util.ssl.SslContextFactory - x509=X509@433defed(testkey,h=[],w=[]) for SslContextFactory@2a693f59(null,null)
INFO  o.e.jetty.server.AbstractConnector - Started ServerConnector@49c90a9c{SSL,[ssl, alpn, h2, http/1.1]}{0.0.0.0:8443}
INFO  org.eclipse.jetty.server.Server - Started @1619ms
```

### Netty

The easiest way to enable HTTP/2 in Netty is to use OpenSSL bindings ([tcnative netty port](https://netty.io/wiki/forked-tomcat-native.html)). 
Add an API jar to dependencies:

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-tcnative</artifactId>
    <version>${tcnative.version}</version>
</dependency>
```

and then  native implementation (statically linked BoringSSL library, a fork of OpenSSL):

```xml
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-tcnative-boringssl-static</artifactId>
        <version>${tcnative.version}</version>
    </dependency>

    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-tcnative-boringssl-static</artifactId>
        <version>${tcnative.version}</version>
        <classifier>${tc.native.classifier}</classifier>
    </dependency>
```

where `tc.native.classifier` should be one of the following: `linux-x86_64`, `osx-x86_64` or `windows-x86_64`.

Once all dependencies have been provided, Ktor will enable HTTP/2 support on the SSL port.

### Tomcat and other servlet containers

Similar to Netty, to get HTTP/2 working in Tomcat you need native OpenSSL bindings. Unfortunately, Tomcat's tcnative is not completely compatible with the Netty one.
This is why you need a slightly different binary. You can get it here (<https://tomcat.apache.org/native-doc/>), or you can try Netty's tcnative. However,
you'll have to guess which exact version is compatible with your specific Tomcat version.

If you are deploying your Ktor application as a war package into the server (servlet container), then you will have to configure your Tomcat server properly:

* <https://tomcat.apache.org/tomcat-8.5-doc/config/http.html#HTTP/2_Support>
* <https://tomcat.apache.org/tomcat-8.5-doc/config/http2.html>
* <https://tomcat.apache.org/tomcat-8.5-doc/ssl-howto.html>
* <https://tomcat.apache.org/native-doc/>