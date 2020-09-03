[//]: # (title: Setup)
[//]: # (caption: Setup)
[//]: # (category: kotlinx)
[//]: # (toc: false)
[//]: # (permalink: /kotlinx/io/setup.html)
[//]: # (ktor_version_review: 1.0.0)

## Gradle

You can use kotlinx-io, with gradle.

Artifacts live in jcenter:

```groovy
repositories {
    jcenter()
}
```

### Common

```groovy
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-io-common:$kotlinx_io_version") // Common
}
```

### JVM

```groovy
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:$kotlinx_io_version") // JVM
}
```

### JavaScript

```groovy
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-io-js:$kotlinx_io_version") // JS
}
```

### Native

```groovy
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-io-native:$kotlinx_io_version") // Native
}
```