# CORS - frontend

A sample application used to make a `fetch` [cross-origin request](https://ktor.io/docs/cors.html) to the [cors-backend](../cors-backend) server. To make a request cross-origin, this sample uses the non-default `5000` port as opposed to the `8080` port used by the [cors-backend](../cors-backend) sample.

## Running

Before running this sample, you need to run [cors-backend](../cors-backend):

```bash
# macOS/Linux
./gradlew :cors-backend:run

# Windows
gradlew.bat :cors-backend:run
```

To run this sample, execute the following command:

```bash
# macOS/Linux
./gradlew :cors-frontend:run

# Windows
gradlew.bat :cors-frontend:run
```

Then, open the [http://localhost:8080/](http://localhost:8080/) page and click the button to make a cross-origin request.
