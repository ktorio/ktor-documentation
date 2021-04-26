# JWT authentication using a JWK provider
A sample project demonstrating how to authenticate a client using a [JSON Web Token](https://ktor.io/docs/jwt.html) signed with RS256. In this example, the [Static](https://ktor.io/docs/serving-static-content.html) feature is used to expose a JWKS endpoint.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-jwk:run

# Windows
gradlew.bat :auth-jwk:run
```

Then, make a `GET` request with a JWT token passed in the `Authorization` header in one of the following ways:
* Open the [get.http](get.http) file and make a request with a token using an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html).
* Execute the following command in a terminal:
   ```Bash
  curl -X GET --location "http://localhost:8080" \
    -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6InB1YmxpYzpjNDI0YjY3Yi1mZTI4LTQ1ZDctYjAxNS1mNzlkYTUwYjViMjEiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJjbGllbnRfaWQiOiJqZXRicmFpbnMiLCJpc3MiOiJodHRwOi8vMC4wLjAuMDo4MDgwLyIsImp0aSI6IjQ1NzM4MmQyLTg0NGMtNDM4OS05YWI4LWRmOWRmMjM4ZTdmOSIsInN1YiI6ImpldGJyYWlucyJ9.B9F2_O9u_J3uoHu6GnZ7FRbq4i68t7P1LRJXse21wm_0QrZ7rganYS_MxFEl8DIoywaIMoA6UveOge8T_az19dVBv3A--EvcKGFy_89XFQVuLe0DnCM2-ZFbtu1H2KCM0aSok2Yn3ktF0auHw8LHXZ17DelznXlgExZwQzTvug-7J3ITHBJhKDkkF5e06dH4xyUbUBW6mmoAqljmuFm238zmN5y5Pp3yWDueWri_MkZog5E0lyGXkJr5LOzFupCPkwVJTVSP4-oTrfD-NCOjdCxiv-6sfdK-h6nJwYfKOTBSHfC0c5DaoWgajdGyoQ0gpNlv2prZeGfsfURkstsKjg"
   ```
