[//]: # (title: Artifacts)
[//]: # (caption: List of Artifacts)
[//]: # (permalink: /quickstart/artifacts.html)
[//]: # (category: quickstart)
[//]: # (redirect_from: redirect_from)
[//]: # (- /artifacts.html: - /artifacts.html)
[//]: # (ktor_version_review: 1.0.1)

Ktor is divided into modules to allow fine-grained inclusion of dependencies based on the functionality required. 
The typical Ktor application would require `ktor-server-core` and a corresponding engine depending on whether it's self-hosted
 or using an Application Server. 

All artifacts in Ktor belong to `io.ktor` group and hosted on JCenter and Maven Central. Pre-release versions are published at [Bintray](https://bintray.com/kotlin/ktor)

[![Download](https://api.bintray.com/packages/kotlin/ktor/ktor/images/download.svg?version=%ktor_version%)](https://bintray.com/kotlin/ktor/ktor/%ktor_version%)
    
Ktor is split into several groups of modules:

* `ktor-server` contains modules that support running the Ktor Application with different engines: Netty, Jetty, Tomcat, and 
a generic servlet. It also contains a TestEngine for setting up application tests without starting the real server
  * `ktor-server-core` is a core package where most of the application API and implementation is located 
  * `ktor-server-jetty` supports a deployed or embedded Jetty instance
  * `ktor-server-netty` supports Netty in embedded mode
  * `ktor-server-tomcat` supports Tomcat servers
  * `ktor-server-servlet` is used by Jetty and Tomcat and allows running in a generic servlet container
  * `ktor-server-test-host` allows running application tests faster without starting the full host
* `ktor-features` groups modules for features that are optional and may not be required by every application
  * `ktor-auth` provides support for different [authentication systems](/servers/features/authentication.html) like Basic, Digest, Forms, OAuth 1a and 2
  * `ktor-auth-jwt` adds the ability to authenticate against [JWT](/servers/features/authentication/jwt.html)
  * `ktor-auth-ldap` adds the ability to authenticate against [LDAP](/servers/features/authentication/ldap.html) instance
  * `ktor-freemarker` integrates Ktor with [Freemarker templates](/servers/features/templates/freemarker.html)
  * `ktor-velocity` integrates Ktor with [Velocity templates](/servers/features/templates/velocity.html)
  * `ktor-gson` integrates with [Gson](/servers/features/content-negotiation/gson.html) adding JSON content negotiation
  * `ktor-jackson` integrates with [Jackson](/servers/features/content-negotiation/jackson.html) adding JSON content negotiation
  * `ktor-html-builder` integrates Ktor with [kotlinx.html builders](/servers/features/templates/html-dsl.html)
  * `ktor-locations` contains experimental support for [typed locations](/servers/features/locations.html)
  * `ktor-metrics` adds the ability to add some [metrics](/servers/features/metrics.html) to the server
  * `ktor-server-sessions` adds the ability to use [stateful sessions stored on a server](/servers/features/sessions.html)
  * `ktor-websockets` provides support for [Websockets](/servers/features/websockets.html)
* `ktor-client` contains modules for [performing http requests](/clients/index.html)
  * `ktor-client-core` is a core package where most of the http HttpClient API is located
  * `ktor-client-apache` adds support for the Apache asynchronous HttpClient
  * `ktor-client-cio`  adds support for a pure Kotlin Corutine based I/O asynchronous HttpClient
  * `ktor-client-jetty` adds support for [Jetty HTTP client](https://www.eclipse.org/jetty/javadoc/current/org/eclipse/jetty/http2/client/HTTP2Client.html)
  * `ktor-client-okhttp` adds support for [OkHttp](https://square.github.io/okhttp/) client backend.
  * `ktor-client-auth-basic` adds support for [authentication](/clients/http-client/features/auth.html)
  * `ktor-client-json` adds support for [json content negotiation](/clients/http-client/features/json-feature.html)
* `ktor-network` includes [raw sockets](/servers/raw-sockets.html) for client/server, and TCP/UDP
  * `ktor-network-tls` contains TLS support for raw sockets
 
See instructions for setting up a project with

* [Maven](/quickstart/quickstart/maven.html)
* [Gradle](/quickstart/quickstart/gradle.html)
* [Generate a project online](/quickstart/generator.html)