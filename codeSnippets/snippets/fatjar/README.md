# Fat JAR

A sample Ktor project configured for generating a fat JAR using the [Gradle Shadow plugin](https://ktor.io/docs/fatjar.html).

## Build a Fat JAR

To build a fat JAR, execute the following command in a repository's root directory:
```bash
./gradlew :fatjar:shadowJar
```
