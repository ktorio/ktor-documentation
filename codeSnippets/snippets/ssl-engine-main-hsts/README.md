# HSTS

A sample Ktor project showing how to use the [HSTS](https://ktor.io/docs/hsts.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running a server, you need to create a keystore. Execute the following command in a repository's root directory:

```Bash
keytool -keystore snippets/ssl-engine-main-hsts/keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US' -ext 'SAN:c=DNS:localhost,IP:127.0.0.1'
```
Enter `foobar` as a password.

To run a sample, execute the following command:
```bash
./gradlew :ssl-engine-main-hsts:run
```

Then, open the [https://localhost:8443](https://localhost:8443) page.
