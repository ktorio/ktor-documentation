# SSL - EngineMain

A sample Ktor project showing how to enable [SSL](https://ktor.io/docs/ssl.html) if a server is configured in `application.conf` or `application.yaml`.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

Before running a server, you need to create a keystore and a truststore. Execute the following commands in the `codeSnippets` directory.

1. Create a keystore:

   ```Bash
   keytool -keystore snippets/ssl-engine-main/keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US' -ext 'SAN:c=DNS:localhost,IP:127.0.0.1'
   ```
   Enter `foobar` as a password.

2. Export the certificate from the keystore:

   ```Bash
   keytool -exportcert -keystore snippets/ssl-engine-main/keystore.jks -alias sampleAlias -storepass foobar -file snippets/ssl-engine-main/sampleAlias.cer
   ```

3. Import the certificate into a truststore:

   ```Bash
   keytool -importcert -keystore snippets/ssl-engine-main/truststore.jks -alias sampleAlias -storepass foobar -file snippets/ssl-engine-main/sampleAlias.cer -noprompt
   ```

To run the sample, use the following command:
```bash
./gradlew :ssl-engine-main:run
```

Then, open the [https://localhost:8443](https://localhost:8443) page.
