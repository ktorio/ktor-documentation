[//]: # (title: Artifacts)

<include src="lib.md" include-id="outdated_warning"/>

Ktor is divided into modules to allow fine-grained inclusion of dependencies based on the functionality required. 
The typical Ktor application would require `ktor-server-core` and a corresponding engine depending on whether it's self-hosted
 or using an Application Server. 

All artifacts in Ktor belong to `io.ktor` group and hosted on [Maven Central](https://mvnrepository.com/artifact/io.ktor). Eap-release versions are published at [jetbrains.space](https://ktor.io/eap)

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
  * `ktor-auth` provides support for different [authentication systems](authentication.md) like Basic, Digest, Forms, OAuth 1a and 2
  * `ktor-auth-jwt` adds the ability to authenticate against [JWT](jwt.md)
  * `ktor-auth-ldap` adds the ability to authenticate against [LDAP](ldap.md) instance
  * `ktor-freemarker` integrates Ktor with [Freemarker templates](freemarker.md)
  * `ktor-velocity` integrates Ktor with [Velocity templates](velocity.md)
  * `ktor-gson` integrates with [Gson](gson.md) adding JSON content negotiation
  * `ktor-jackson` integrates with [Jackson](jackson.md) adding JSON content negotiation
  * `ktor-html-builder` integrates Ktor with [kotlinx.html builders](kotlin_serialization.md)
  * `ktor-locations` contains experimental support for [typed locations](locations.md)
  * `ktor-metrics` adds the ability to add some [metrics](dropwizard_metrics.md) to the server
  * `ktor-server-sessions` adds the ability to use [stateful sessions stored on a server](sessions.md)
  * `ktor-websockets` provides support for [Websockets](websocket.md)
* `ktor-client` contains modules for [performing http requests](client.md)
  * `ktor-client-core` is a core package where most of the http HttpClient API is located
  * `ktor-client-apache` adds support for the Apache asynchronous HttpClient
  * `ktor-client-cio`  adds support for a pure Kotlin Corutine based I/O asynchronous HttpClient
  * `ktor-client-jetty` adds support for [Jetty HTTP client](https://www.eclipse.org/jetty/javadoc/current/org/eclipse/jetty/http2/client/HTTP2Client.html)
  * `ktor-client-okhttp` adds support for [OkHttp](https://square.github.io/okhttp/) client backend.
  * `ktor-client-auth-basic` adds support for [authentication](features_auth.md)
  * `ktor-client-json` adds support for [json content negotiation](json-feature.md)
* `ktor-network` includes [raw sockets](servers_raw-sockets.md) for client/server, and TCP/UDP
  * `ktor-network-tls` contains TLS support for raw sockets
 
See instructions for setting up a project with

* [Maven](Maven.md)
* [Gradle](Gradle.md)
* [Generate a project online](generator.md)