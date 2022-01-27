# Client SSL config

A sample Ktor project showing how to configure SSL settings for different [engines](https://ktor.io/docs/http-client-engines.html).

## Running

Before running a server, you need to create a keystore. Execute the following command in a repository's root directory:

```Bash
keytool -keystore snippets/ssl-engine-main/keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US' -ext 'SAN:c=DNS:localhost,IP:127.0.0.1'
```
Enter `foobar` as a password.

Then, start a server from the [ssl-engine-main](../ssl-engine-main) example:
```bash
./gradlew :ssl-engine-main:run
```

In this example, the Ktor server serves SSL directly using a self-signed certificate.

Then, run this sample to make a request and get a response.

```bash
./gradlew :client-ssl-config:run
```
