# Client digest authentication

A sample project demonstrating how to authenticate a client using digest [authentication](https://ktor.io/docs/auth.html).

## Running

Before running this sample, start the [auth-digest](../auth-digest) server sample with a resource protected using digest authentication:

```bash
# macOS/Linux
./gradlew :auth-digest:run

# Windows
gradlew.bat :auth-digest:run
```

Then, run this client sample:

```bash
# macOS/Linux
./gradlew :client-auth-digest:run

# Windows
gradlew.bat :client-auth-digest:run
```

A server should respond with the 'Hello, jetbrains!' message.