# CORS - backend

A sample project demonstrating how to configure [CORS](https://ktor.io/docs/cors.html) on a Ktor server. You can test this sample using the [cors-frontend](../cors-frontend) application, which allows you to make a `fetch` cross-origin request.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :cors-backend:run

# Windows
gradlew.bat :cors-backend:run
```

To run the [cors-frontend](../cors-frontend) sample, execute the following command:

```bash
# macOS/Linux
./gradlew :cors-frontend:run

# Windows
gradlew.bat :cors-frontend:run
```

Then, open the [http://localhost:5000/](http://localhost:5000/) page and click the button to make a cross-origin request.
