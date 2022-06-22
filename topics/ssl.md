[//]: # (title: SSL and certificates)

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-network-tls-certificates</code>
</p>
<p>
<b>Code examples</b>: 
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main">ssl-engine-main</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-embedded-server">ssl-embedded-server</a>
</p>
</microformat>

In most cases, your Ktor services are placed behind a reverse proxy such as Nginx or Apache. 
This means that a reverse proxy server handles security concerns, including SSL.

If necessary, you can configure Ktor to serve SSL directly by providing a path to a certificate.
Ktor uses [Java KeyStore (JKS)](https://docs.oracle.com/javase/8/docs/api/java/security/KeyStore.html) as a storage facility for certificates.
You can use [keytool](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html) to convert and manage certificates stored in KeyStore.
This might be useful if you need to convert a PEM certificate issued by a certificate authority to the JKS format supported by Ktor.

> You can use _Let's Encrypt_ to get a free certificate to serve `https://` and `wss://` requests with Ktor.

## Generate a self-signed certificate {id="self-signed"}
### Generate a certificate in code {id="self-signed-code"}

Ktor provides the ability to generate self-signed certificates for testing purposes by calling the [generateCertificate](https://api.ktor.io/ktor-network/ktor-network-tls/ktor-network-tls-certificates/io.ktor.network.tls.certificates/generate-certificate.html) function, which returns a [KeyStore](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/security/KeyStore.html) instance. To use this function, you need to add the `ktor-network-tls-certificates` artifact in the build script:

<var name="artifact_name" value="ktor-network-tls-certificates"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

The code snippet below shows how to generate a certificate and save it to a keystore file:

```kotlin
```
{src="snippets/ssl-embedded-server/src/main/kotlin/com/example/Application.kt" lines="12-19,38"}

Since Ktor requires a certificate when it starts, you have to create a certificate before starting the server. 
You can find the full example here: [ssl-embedded-server](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-embedded-server).

### Generate a certificate using keytool {id="self-signed-keytool"}

You can use [keytool](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html) to generate a self-singed certificate:

```Bash
keytool -keystore keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```

After executing this command, `keytool` suggests you specify a keystore password and then generates a JKS file.

## Convert PEM certificates to JKS {id="convert-certificate"}

If your certificate authority issues certificates in a PEM format, you need to convert it to the JKS format before [configuring SSL in Ktor](#configure-ssl-ktor).
You can use `openssl` and `keytool` utilities to do this. 
For example, if you have a private key in the `key.pem` file and a public certificate in `cert.pem`, conversion process might look as follows:

1. Convert PEM into the PKCS12 format using `openssl` by using the following command:
   ```Bash
   openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name "sampleAlias"
   ```
   You will be prompted to enter a passphrase for `key.pem` and a new password for `keystore.p12`.

2. Convert PKCS12 to the JKS format using `keytool`:
   ```Bash
   keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore keystore.jks
   ```
   You will be prompted to enter a password for the `keystore.p12` file and a new password for `keystore.jks`.
   The `keystore.jks` will be generated.


## Configure SSL in Ktor {id="configure-ssl-ktor"}
Specifying SSL settings in Ktor depends on the way used to [configure a Ktor server](create_server.xml): by using a configuration file or in code using the `embeddedServer` function.

### Configuration file {id="config-file"}

If your server is configured in the `application.conf` or `application.yaml` [configuration file](Configurations.xml#configuration-file), you can enable SSL using the following [properties](Configurations.xml#predefined-properties):

1. Specify the SSL port using the `ktor.deployment.sslPort` property:

   <tabs group="config">
   <tab title="application.conf" group-key="hocon">
   
   ```shell
   ```
   {style="block" src="snippets/ssl-engine-main/src/main/resources/application.conf" lines="1-2,4-5,18"}
   
   </tab>
   <tab title="application.yaml" group-key="yaml">
   
   ```yaml
   ```
   {style="block" src="snippets/ssl-engine-main/src/main/resources/_application.yaml" lines="1-2,4"}
   
   </tab>
   </tabs>

2. Provide keystore settings in a separate `security` group:

   <tabs group="config">
   <tab title="application.conf" group-key="hocon">
   
   ```shell
   ```
   {style="block" src="snippets/ssl-engine-main/src/main/resources/application.conf" lines="1,10-18"}
   
   </tab>
   <tab title="application.yaml" group-key="yaml">
   
   ```yaml
   ```
   {style="block" src="snippets/ssl-engine-main/src/main/resources/_application.yaml" lines="1,9-14"}
   
   </tab>
   </tabs>


You can find the full example here: [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main).

### embeddedServer {id="embedded-server"}

If you use the `embeddedServer` function to run your server, you need to pass a [custom environment](Configurations.xml#embedded-custom) and provide SSL settings there using [sslConnector](https://api.ktor.io/ktor-server/ktor-server-host-common/io.ktor.server.engine/ssl-connector.html):
```kotlin
```
{src="snippets/ssl-embedded-server/src/main/kotlin/com/example/Application.kt" lines="21-37"}

You can find the full example here: [ssl-embedded-server](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-embedded-server).
