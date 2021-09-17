[//]: # (title: FAQ)

<include src="lib.xml" include-id="outdated_warning"/>

In this section, we provide answers to the questions you frequently ask us.

>Can't find an answer for your question? Head over our #ktor [Kotlin Slack](http://slack.kotlinlang.org/){ target="_blank" rel="noopener"} channel,
>and we will try to help you!
>
{type="note"}





## What is the proper way to pronounce ktor?
{id="pronounce"}

*kay-tor*

## How do I put questions, report bugs, contact you, contribute, give feedback, etc.?
{id="feedback"}

Depending on the content, you might consider several channels:

* **GitHub:** Feature request, change suggestions/proposals, bugs and PRs.
* **Slack:** Questions, troubleshooting, guidance etc.
* **StackOverflow:** Questions.

**Rationale:**

For questions or troubleshooting we highly recommend you to use Slack or StackOverflow.

Think that using GitHub issues would notify all the people that is subscribed potentially with emails,
troubleshooting usually requires several questions and answers, that could be a lot of emails
spread in the time, and maybe the people subscribed want to be informed about bugs, fixes, new things introduced
or proposed, but maybe they are not interested in other things.

If you have enough time, or you prefer not to join Slack, you can also ask questions at StackOverflow.
With Slack being a hybrid between chat and forum, we can contact each other faster and troubleshoot things in less time.

When troubleshooting, if we determine that there is a bug, or something to improve, you can report it a GitHub.
Of course, it is not a good idea either to keep a bug report (once confirmed) just in Slack since it could be forgotten,
so lets put them in GitHub.

**Pull Requests:**

If you have a functionality or bugfix you think would be worth including in Ktor, you can create a PR.

Have in mind that we usually review and merge PRs in batches, so the PR could be outstanding for a few weeks.
But still we encourage you to contribute if you can!

## What does CIO mean?
{id="cio"}

CIO stands for Coroutine-based I/O. Usually we call it to an engine that uses Kotlin and Coroutines to implement
the logic implementing an IETF RFC or another protocol without relying on external JVM-based libraries.

## Ktor imports are not being resolved. Imports are in red.
{id="ktor-artifact"}

Ensure that you are including the Ktor artifacts. For example, for Gradle and the Netty engine it would be:
```groovy
dependencies {
    implementation "io.ktor:ktor-server-core:%ktor_version%"
    implementation "io.ktor:ktor-server-netty:%ktor_version%"
}
```
{interpolate-variables="true"}


* For Gradle, check [](Gradle.xml)
* For Maven, check [](Maven.xml)

## Does ktor provide a way to catch IPC signals (e.g. SIGTERM or SIGINT) so the server shutdown can be handled gracefully?
{id="sigterm"}

If you are running an [EngineMain](create_server.xml#engine-main), it will be handled automatically.
Otherwise, you need to [handle it manually](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-cio/jvm/src/io/ktor/server/cio/EngineMain.kt#L21).
You can use `Runtime.getRuntime().addShutdownHook` JVM's facility.

## How do I get the client IP behind a proxy?
{id="proxy-ip"}

The property `call.request.origin` gives connection information about the original caller (the proxy) if the proxy provides proper headers, and the plugin `XForwardedHeaderSupport` is installed.

## I get the error 'java.lang.IllegalStateException: No instance for key AttributeKey: Locations'
{id="no-attribute-key-locations"}

You get this error if you try to use the [Locations](locations.md) plugin without actually installing it. 

## How can I test the latest commits on main?
{id="bleeding-edge"}

You can use jitpack to get builds from main that are not yet released:
<https://jitpack.io/#ktorio/ktor>
Also, you can [build Ktor from source](build-from-source.md), and use your `mavenLocal` repository for the artifact
or to upload your artifacts to your own artifactory.

## How can I be sure of which version of Ktor am I using?
{id="ktor-version-used"}

You can use the [DefaultHeaders](default_headers.md) plugin that will send a
Server header with the Ktor version on it.
Something similar to `Server: ktor-server-core/%ktor_version%` should be sent as part of the response headers.


## My route is not being executed, how can I debug it?
{id="route-not-executing"}

Ktor provides a tracing mechanism for the routing plugin to help troubleshooting
routing decisions. Check the [Tracing the routing decisions](tracing_routes.md) section in the Routing page.

## I get a `io.ktor.pipeline.InvalidPhaseException: Phase Phase('YourPhase') was not registered for this pipeline`.
{id="invalid-phase"}

This means that you are trying to use a phase that is not registered as a reference for another phase.
This might happen for example in the Routing plugin if you try to register a phase relation inside a node,
but the phase referenced is defined in another ancestor Route node. Since route phases and interceptors are later
merged, it should work, but you need to register it in your Route node:

```kotlin
route.addPhase(PhaseDefinedInAncestor)
route.insertPhaseAfter(PhaseDefinedInAncestor, MyNodePhase)
```

## I get a `io.ktor.server.engine.BaseApplicationResponse$ResponseAlreadySentException: Response has already been sent`
{id="response-already-sent"}

This means that you, or a plugin or interceptor, have already called `call.respond*` functions, and you are calling it
again.

## How can I subscribe to Ktor events?
{id="ktor-events"}

There is a page [explaining the Ktor's application-level event system](events.md).

## I get a `Exception in thread "main" com.typesafe.config.ConfigException$Missing: No configuration setting found for key 'ktor'` exception
{id="cannot-find-application-conf"}

This means that Ktor was not able to find the `application.conf` file. Re-check that it is in the `resources` folder,
and that the `resources` folder is marked as such.
You can consider to set up a project using the [project generator](https://start.ktor.io/) or the [IntelliJ plugin](intellij-idea.xml)
to have a working project as base.

## Can I use ktor on Android?
{id="android-support"}

Ktor is known to work on Android 7 or greater (API 24). It will fail in lower versions like Android 5.

In unsupported versions it would fail with an exception similar to:

```text
E/AndroidRuntime: FATAL EXCEPTION: main Process: com.mypackage.example, PID: 4028 java.lang.NoClassDefFoundError: 
io.ktor.application.ApplicationEvents$subscribe$1 at io.ktor.application.ApplicationEvents.subscribe(ApplicationEvents.kt:18) at 
io.ktor.server.engine.BaseApplicationEngine.<init>(BaseApplicationEngine.kt:29) at 
io.ktor.server.engine.BaseApplicationEngine.<init>(BaseApplicationEngine.kt:15) at 
io.ktor.server.netty.NettyApplicationEngine.<init>(NettyApplicationEngine.kt:17) at io.ktor.server.netty.Netty.create(Embedded.kt:10) at 
io.ktor.server.netty.Netty.create(Embedded.kt:8) at io.ktor.server.engine.EmbeddedServerKt.embeddedServer(EmbeddedServer.kt:50) at 
io.ktor.server.engine.EmbeddedServerKt.embeddedServer(EmbeddedServer.kt:40) at 
io.ktor.server.engine.EmbeddedServerKt.embeddedServer$default(EmbeddedServer.kt:27)
```

For more information, check [Issue #495](https://github.com/ktorio/ktor/issues/495) and [StackOverflow
question](https://stackoverflow.com/questions/49945584/attempting-to-run-an-embedded-ktor-http-server-on-android)

## CURL -I returns a 404 Not Found
{id="curl-head-not-found"}

`CURL -I` that is an alias of `CURL --head` that performs a `HEAD` request.
By default, Ktor doesn't handle `HEAD` requests for `GET` handlers, so you might get something like:

```HTTP
curl -I http://localhost:8080
HTTP/1.1 404 Not Found
Content-Length: 0
```

for:

```kotlin
routing {
    get("/") { call.respondText("HELLO") }
}
```

Ktor can automatically handle `HEAD` requests, but requires you to first install the [AutoHeadResponse](autoheadresponse.md) plugin.

```kotlin
install(AutoHeadResponse) 
```

## I get an infinite redirect when using the `HttpsRedirect` plugin
{id="infinite-redirect"}

The most probable cause is that your backend is behind a reverse-proxy or a load balancer, and that this intermediary
is making normal HTTP requests to your backend, thus the HttpsRedirect plugin inside your Ktor backend believes
that it is a normal HTTP request and responds with the redirect.

Normally, reverse-proxies send some headers describing the original request (like it was HTTPS, or the original IP address),
and there is a plugin [`XForwardedHeaderSupport`](forward-headers.md)
to parse those headers so the [`HttpsRedirect`](https-redirect.md) plugin knows that the original request was HTTPS.
