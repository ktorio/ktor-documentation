[//]: # (title: Building Ktor)
[//]: # (caption: Building Ktor From Source)
[//]: # (category: advanced)
[//]: # (keywords: git compiling compile ktor from source)
[//]: # (permalink: /advanced/building-from-source.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor is an OpenSource project hosted at GitHub:
<https://github.com/ktorio/ktor>

Release versions are available at both jcenter and maven central.

We usually provide binary version previews at bintray:
<https://bintray.com/kotlin/ktor/ktor>

Additionally, you can use jitpack to get bleeding edge artifacts compiled from master:
<https://jitpack.io/#ktorio/ktor>

## Downloading the source
{id="get-git-sources"}

You can get the latest version of Ktor using git to clone Ktor's repository:

```text
git clone https://github.com/ktorio/ktor.git
cd ktor
```

## Building
{id="building"}

Ktor uses gradle for building. It should work with any gradle version
greater than 4.3, but for best results we provide a gradle wrapper,
which should work with any supported system with a JDK installed: 

```text
./gradlew build
```

## Installing locally
{id="installing"}

Ktor provides a gradle install task that installs Ktor artifacts in your
local maven repository:

```text
./gradlew publishToMavenLocal
```

## Troubleshooting
{id="troubleshooting"}

If you get an error similar to:

```text
* Where:
Build file '/.../ktor/ktor-server/ktor-server-benchmarks/build.gradle' line: 2

* What went wrong:
An exception occurred applying plugin request [id: 'me.champeau.gradle.jmh', version: '0.4.4']
> Failed to apply plugin [id 'me.champeau.gradle.jmh']
   > Could not generate a proxy class for class me.champeau.gradle.JMHPluginExtension.
```

You might have forgotten to use the gradle wrapper (`./gradlew`), or your default installed
gradle version is lower than 4.3.

Always use the gradle wrapper for consistent results!