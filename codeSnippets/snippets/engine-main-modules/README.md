# EngineMain - Modules

A sample Ktor project showing how to load specified [modules](https://ktor.io/docs/modules.html) when a server configuration is stored in a configuration file.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :engine-main-modules:run
```

Then, open the following pages to check that all modules are loaded:
* [http://localhost:8080/module1](http://localhost:8080/module1)
* [http://localhost:8080/module2](http://localhost:8080/module2)
* [http://localhost:8080/module3](http://localhost:8080/module3)