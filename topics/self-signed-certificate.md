[//]: # (title: Self-Signed Certificate)

<include src="lib.md" include-id="outdated_warning"/>

Ktor allows you to create and use self-signed certificates for serving HTTPS or HTTP/2 requests.
To create a self-signed certificate using Ktor, you have to call the `generateCertificate` function.

```kotlin
io.ktor.network.tls.certificates.generateCertificate(File("mycert.jks"))
```
To use this function, you need to add the `ktor-network-tls-certificates` artifact in the build script:

<var name="artifact_name" value="ktor-network-tls-certificates"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

Since Ktor requires the certificate when it starts, you have to create the certificate before starting the server.

## Create the certificate using gradle

One possible option is to execute the main class generating the certificate before actually running the server:

### `CertificateGenerator.kt`

You can declare a class with a main method that only generates the certificate when it doesn't exist:

```kotlin
package io.ktor.samples.http2

import io.ktor.network.tls.certificates.generateCertificate
import java.io.File

object CertificateGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val jksFile = File("build/temporary.jks").apply {
            parentFile.mkdirs()
        }

        if (!jksFile.exists()) {
            generateCertificate(jksFile) // Generates the certificate
        }
    }
}

```

### `build.gradle`

In your `build.gradle` file you can make the `run` task to depend on a `generateJks` task that executes the main
class generating the certificate. For example:

```groovy
task generateJks(type: JavaExec, dependsOn: 'classes') {
    classpath = sourceSets.main.runtimeClasspath
    main = 'io.ktor.samples.http2.CertificateGenerator'
}

getTasksByName("run", false).first().dependsOn('generateJks')
```

## The HOCON `application.conf` configuration file

When creating your HOCON configuration file, you have to add the `ktor.deployment.sslPort`, and the `ktor.security.ssl`
properties to define the ssl port and the keyStore:

`resources/application.conf`:

```kotlin
```
{src="snippets/_misc/SslExample.conf"}

## Ktor normal module

After that you can just write a normal plain Ktor module: 


```kotlin
package io.ktor.samples.http2

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import java.io.*

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            call.push("/style.css")

            call.respondText("""
                <!DOCTYPE html>
                <html>
                    <head>
                        <link rel="stylesheet" type="text/css" href="/style.css">
                    </head>
                    <body>
                        <h1>Hello, World!</h1>
                    </body>
                </html>
            """.trimIndent(), contentType = ContentType.Text.Html)
        }

        get("/style.css") {
            call.respondText("""
                h1 { color: olive }
            """, contentType = ContentType.Text.CSS)
        }
    }
}
```

## Accessing your server

Then you can point to <https://127.0.0.1:8443/> to access your server.
Since this is a self-signed certificate, your browser will probably warn you about an invalid certificate, so
you will have to disable that warning.

## Full example

Ktor has a full example using self-signed certificates here:

<https://github.com/ktorio/ktor/tree/08b173e02fe9a9dbee39f48e7162e6ea7a1f8b16/ktor-samples/ktor-samples-ssl-http2>