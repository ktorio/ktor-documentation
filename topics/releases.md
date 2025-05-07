[//]: # (title: Ktor releases)

<show-structure for="chapter" depth="2"/>

Ktor follows [Semantic Versioning](https://semver.org/):

- _Major versions_ (x.0.0) contain incompatible API changes.
- _Minor versions_ (x.y.0) provide backward-compatible new functionality.
- _Patch versions_ (x.y.z) contain backward-compatible fixes.

For each major and minor release, we also ship several preview (EAP) versions for you to try new features before they
are released. For more details, see [Early Access Program](https://ktor.io/eap/).

## Gradle plugin {id="gradle"}

The [Gradle Ktor plugin](https://github.com/ktorio/ktor-build-plugins) and the framework are in the same release cycle.
You can find all plugin releases on the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.ktor.plugin).

## IntelliJ Ultimate plugin {id="intellij"}

The [IntelliJ Ktor plugin](https://www.jetbrains.com/help/idea/ktor.html) is released independently of the Ktor
framework and
uses the same release cycle as [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download/other.html).

### Update to a new release {id="update"}

The IntelliJ Ktor plugin allows you to migrate your Ktor project to the latest version.
You can learn more from the [Migrate projects](https://www.jetbrains.com/help/idea/ktor.html#migrate) section.

## Release details {id="release-details"}

The following table lists details of the latest Ktor releases.

<table>
<tr><td>Version</td><td>Release Date</td><td>Highlights</td></tr>
<tr><td>3.1.3</td><td>May 5, 2025</td><td><p>
A patch release including performance improvements like faster
<a href="https://youtrack.jetbrains.com/issue/KTOR-8412">
byte operations
</a>
and
<a href="https://youtrack.jetbrains.com/issue/KTOR-8407">
multipart handling
</a>
, and
<a href="https://youtrack.jetbrains.com/issue/KTOR-8107">
safer token refresh handling
</a>
. It also fixes 
<a href="https://youtrack.jetbrains.com/issue/KTOR-8276">
memory issues in metrics
</a>
,
<a href="https://youtrack.jetbrains.com/issue/KTOR-8326">
improves header behavior
</a>
, and resolves bugs across WebSockets, OkHttp, Apache5, and Netty, plus 
<a href="https://youtrack.jetbrains.com/issue/KTOR-8030">
updates JTE for Kotlin 2.1.0 support
</a>.
</p>
<var name="version" value="3.1.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.1.2</td><td>March 27, 2025</td><td><p>
A patch release that updates Kotlin to 2.1.20 and fixes various issues, including Base64 decoding, auth token clearing,
Android server startup errors, WebSocket header formatting, and SSE session cancellation. 
</p>
<var name="version" value="3.1.2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.1.1</td><td>February 24, 2025</td><td><p>
A patch release improving logging and fixing WebSocket timeout handling. It fixes multiple bugs, including HTTP cache 
inconsistencies, form data copying errors, gzip handling crashes, and concurrency issues causing segment pool corruption.
</p>
<var name="version" value="3.1.1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.1.0</td><td>February 11, 2025</td><td><p>
A minor release introducing various SSE features and extended CIO engine and WebSocket support. It enhances platform
compatibility, logging, and authentication while fixing critical bugs related to byte channel handling,
HTTP request failures, and concurrency issues.
</p>
<var name="version" value="3.1.0"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.3</td><td>December 18, 2024</td><td><p>
A patch release with various bug fixes, including fixing build errors in <code>browserProductionWebpack</code>,
gzipped content handling, and <code>FormFieldLimit</code> configuration overwrites. This release also includes core 
performance improvements and proper test application shutdown.
</p>
<var name="version" value="3.0.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.2</td><td>December 4, 2024</td><td><p>
A patch release addressing multiple bug fixes related to response corruption, truncated bodies, connection handling, 
and incorrect headers, along with extended binary encoding support and performance enhancements for Android. 
</p>
<var name="version" value="3.0.2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.13</td><td>November 20, 2024</td><td><p>
A patch release with bug fixes, security patches, and improvements, including added support for the
<code>watchosDeviceArm64</code> target.  
</p>
<var name="version" value="2.3.13"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.1</td><td>October 29, 2024</td><td><p>
A patch release including improvements in client and server logging, and various bug fixes.  
</p>
<var name="version" value="3.0.1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.0</td><td>October 9, 2024</td><td><p>
A major release containing improvements and bug fixes, including added support for Android Native targets.
For more information on breaking changes, see <a href="https://ktor.io/docs/migrating-3.html">the migration guide</a>.
</p>
<var name="version" value="3.0.0"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.0-rc-2</td><td>October 2, 2024</td><td><p>
A major release candidate containing various improvements with breaking changes, bug fixes, and features, such as
multiplatform support for XML.
</p>
<var name="version" value="3.0.0-rc-2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.0-rc-1</td><td>September 9, 2024</td><td><p>
A major release candidate containing significant improvements and bug fixes. This update enhances backward compatibility
and features extended <code>staticZip</code> support.
For more information on breaking changes, see <a href="https://ktor.io/docs/eap/migrating-3.html">the migration guide</a>.
</p>
<var name="version" value="3.0.0-rc-1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.0-beta-2</td><td>July 15, 2024</td><td><p>
A major pre-release version with various improvements and bug fixes, including SSE support improvements and a Ktor client for Kotlin/Wasm.
For more information on breaking changes, see <a href="https://ktor.io/docs/3.0.0-beta-2/migrating-3.html">the migration guide</a>.
</p>
<var name="version" value="3.0.0-beta-2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.12</td><td>June 20, 2024</td><td><p>
A patch release, including bug fixes in Ktor Core and Ktor Server, as well as version updates for Netty and OpenAPI.
</p>
<var name="version" value="2.3.12"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.11</td><td>May 9, 2024</td><td><p>
A patch release, including a bug fix for applying a socket timeout to the Test Client's engine.
</p>
<var name="version" value="2.3.11"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.10</td><td>April 8, 2024</td><td><p>
A patch release including various bug fixes for the CallLogging and SSE server plugins, improved Android client logging, and more.
</p>
<var name="version" value="2.3.10"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.9</td><td>March 4, 2024</td><td><p>
A patch release including a bug fix for the ContentNegotiation client plugin, and an added support for sending secure cookies over HTTP.
</p>
<var name="version" value="2.3.9"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.8</td><td>January 31, 2024</td><td><p>
A patch release including various bug fixes for URLBuilder, CORS, and WebSocket plugins.
</p>
<var name="version" value="2.3.8"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.7</td><td>December 7, 2023</td><td>
<p>
A patch release including bug fixes in ContentNegotiation, WebSockets, and the memory usage in Native Server.
</p>
<var name="version" value="2.3.7"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>3.0.0-beta-1</td><td>November 23, 2023</td><td>
<p>
A major pre-release version with various improvements and bug fixes, including client and server SSE support.
</p>
<var name="version" value="3.0.0-beta-1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.6</td><td>November 7, 2023</td><td>
<p>
A patch release, including a fix for a breaking change in <code>2.3.5</code> and various other bug fixes.
</p>
<var name="version" value="2.3.6"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.5</td><td>October 5, 2023</td><td>
<p>
A patch release, including fixes in Darwin and Apache5 engine configurations.
</p>
<var name="version" value="2.3.5"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.4</td><td>August 31, 2023</td><td>
<p>
A patch release, including a bug fix in the HTTP Cookie header and the NoTransformationFoundException error.
</p>
<var name="version" value="2.3.4"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.3</td><td>August 1, 2023</td><td>
<p>
A patch release that includes client and server support for <code>linuxArm64</code> and various bug fixes.
</p>
<var name="version" value="2.3.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.2</td><td>June 28, 2023</td><td>
<p>
A patch release with upgraded Kotlin version to <code>1.8.22</code> and various bug fixes.
</p>
<var name="version" value="2.3.2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.1</td><td>May 31, 2023</td><td>
<p>
A patch release including improvements in server configurations and various bug fixes.
</p>
<var name="version" value="2.3.1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.3.0</td><td>April 19, 2023</td><td>
<p>
A feature release adding support for multiple configuration files, regex patterns in Routing, and more.
</p>
<var name="version" value="2.3.0"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.2.4</td><td>February 28, 2023</td><td>
<p>
A patch release containing various bug fixes in the HTTP client, Routing, and ContentNegotiation.
</p>
<var name="version" value="2.2.4"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.2.3</td><td>January 31, 2023</td><td>
<p>
A patch release, including multiplatform functionality for OAuth2 and various bug fixes.
</p>
<var name="version" value="2.2.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.2.2</td><td>January 3, 2023</td><td>
<p>
A patch release including a bug fix for <code>2.2.1</code>, improvements and fixes in the Swagger plugin and more.
</p>
<var name="version" value="2.2.2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.2.1</td><td>December 7, 2022</td><td>
<p>
A patch release for the <code>java.lang.NoClassDefFoundError: kotlinx/atomicfu/AtomicFU</code> error in <code>2.2.0</code>.
</p>
<var name="version" value="2.2.1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.2.0</td><td>December 7, 2022</td><td>
<p>
A multiple feature release, including Swagger UI hosting, new plugins API, multiplatform support for Sessions, and more.
For more information, see the <a href="migration-to-22x.md">Migrating from 2.0.x to 2.2.x</a> guide.
</p>
<var name="version" value="2.2.0"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.1.3</td><td>October 26, 2022</td><td>
<p>
A patch release with various bug fixes.
</p>
<var name="version" value="2.1.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.1.2</td><td>September 29, 2022</td><td>
<p>
A patch release that includes bug fixes in Routing, Testing engine, and Ktor client.
</p>
<var name="version" value="2.1.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.1.1</td><td>September 6, 2022</td><td>
<p>
A patch release with various bug fixes in Ktor client and server.
</p>
<var name="version" value="2.1.1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.1.0</td><td>August 11, 2022</td><td>
<p>
A minor release, adding support for YAML configuration and various other improvements and bug fixes.
</p>
<var name="version" value="2.1.0"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.0.3</td><td>June 28, 2022</td><td>
<p>
A patch release containing bug fixes and upgraded <code>kotlinx.coroutines</code> version to <code>1.6.2</code>.
</p>
<var name="version" value="2.0.3"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.0.2</td><td>May 27, 2022</td><td>
<p>
A patch release containing various improvements, bug fixes, and dependencies version upgrades.
</p>
<var name="version" value="2.0.2"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.0.1</td><td>April 28, 2022</td><td>
<p>
A patch release with various bug fixes and updated Kotlin version to <code>1.6.21</code>.
</p>
<var name="version" value="2.0.1"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>2.0.0</td><td>April 11, 2022</td><td>
<p>
A major release with updated API docs and various new features. For more information on braking changes and how to
migrate from <code>1.x.x</code>, see <a href="migration-to-20x.md">the migration guide</a>.
</p>
<var name="version" value="2.0.0"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
<tr><td>1.6.8</td><td>March 15, 2022</td><td>
<p>
A patch release containing dependencies version upgrades.
</p>
<var name="version" value="1.6.8"/>
<include from="lib.topic" element-id="release_details_link"/>
</td></tr>
</table>


