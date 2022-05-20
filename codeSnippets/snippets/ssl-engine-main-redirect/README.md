# HTTPS Redirect

A sample Ktor project showing how to use the [HttpsRedirect](https://ktor.io/docs/https-redirect.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running a server, you need to create a keystore. Execute the following command in a repository's root directory:

```Bash
keytool -keystore snippets/ssl-engine-main-redirect/keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US' -ext 'SAN:c=DNS:localhost,IP:127.0.0.1'
```
Enter `foobar` as a password.

To run a sample, execute the following command:
```bash
./gradlew :ssl-engine-main-redirect:run
```

Then, open the [http://0.0.0.0:8080](http://0.0.0.0:8080) page to make sure `HttpsRedirect` redirects to [https://0.0.0.0:8443](https://0.0.0.0:8443).
