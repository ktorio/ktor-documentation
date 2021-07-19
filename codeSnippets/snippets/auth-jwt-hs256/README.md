# JWT authentication with HS256
A sample project demonstrating how to authenticate a client using a [JSON Web Token](https://ktor.io/docs/jwt.html) signed with HS256.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-jwt-hs256:run

# Windows
gradlew.bat :auth-jwt-hs256:run
```

Then, open the [requests.http](requests.http) file and do the following:
1. Run the first `POST` request to `http://localhost:8080/login` to emulate login process. A server will generate and return a token. Note that after receiving a response HTTP Client saves a generated token in a [variable](https://www.jetbrains.com/help/idea/http-response-handling-examples.html#script-var-example), which will be used to authorize the client in the second request. 
2. Run the second `GET` request with a token sent using the `Bearer` scheme and access the protected `hello` resource.
