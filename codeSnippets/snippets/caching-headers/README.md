# Caching headers
A sample project demonstrating how to configure [caching headers](https://ktor.io/docs/caching.html) in Ktor.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :caching-headers:run

# Windows
gradlew.bat :caching-headers:run
```

Then, you can open a browser's network tools for [http://localhost:8080/html-dsl](http://localhost:8080/html-dsl) and reload this page to make sure the `styles.css` file is served from memory cache.