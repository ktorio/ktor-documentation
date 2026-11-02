# Client SSL config

A sample Ktor project showing how to configure SSL settings for different [engines](https://ktor.io/docs/http-client-engines.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

Before running this client, you need to create a keystore, a truststore, and run the [ssl-engine-main](../ssl-engine-main) server.
Execute the following commands in the `codeSnippets` directory.

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
   
4. Copy the keystore created in the first step to the client sample:
   ```Bash
   cp snippets/ssl-engine-main/keystore.jks snippets/client-ssl-config/keystore.jks
   ```
   
5. Start the server from the [ssl-engine-main](../ssl-engine-main) example:

   ```bash
   ./gradlew :ssl-engine-main:run
   ```

6. Run the client to make a request and get a response:
   ```bash
   ./gradlew :client-ssl-config:run
   ```
