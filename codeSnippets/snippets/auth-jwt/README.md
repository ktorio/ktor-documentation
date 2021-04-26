# JWT authentication
A sample project demonstrating how to use [JSON Web Tokens](https://ktor.io/docs/jwt.html) for authentication.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-jwt:run

# Windows
gradlew.bat :auth-jwt:run
```

Then, make a `GET` request with a JWT token passed in the `Authorization` header in one of the following ways:
* Open the [get.http](get.http) file and make a request with a token using an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html).
* Execute the following command in a terminal:
   ```Bash
  curl -X GET --location "http://localhost:8080" \
    -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2p3dC1wcm92aWRlci1kb21haW4vIiwiYXVkIjoiand0LWF1ZGllbmNlIiwic3ViIjoiamV0YnJhaW5zIn0.ZT5cX_2XHn_nrhhghfeP_cABSifYZrEtzoKKtQiwCN0"
   ```