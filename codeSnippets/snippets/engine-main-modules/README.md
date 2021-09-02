# EngineMain - Modules

A sample Ktor project showing how to load specified [modules](https://ktor.io/docs/modules.html) when a server configuration is stored in a `HOCON` file.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :engine-main-modules:run
```

Then, open the following pages to check that all modules are loaded:
* [http://0.0.0.0:8080/module1](http://0.0.0.0:8080/module1)
* [http://0.0.0.0:8080/module2](http://0.0.0.0:8080/module2)
* [http://0.0.0.0:8080/module3](http://0.0.0.0:8080/module3)