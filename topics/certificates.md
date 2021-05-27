[//]: # (title: Certificates)

<include src="lib.xml" include-id="outdated_warning"/>

## JKS file format

JKS (Java KeyStore) is the certificate format used by Java and Ktor.

You can use [`keytool`](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/keytool.html) to convert and manage these kind of certificates.

## CER file format

```bash
keytool -import -v -trustcacerts -alias keyAlias -file server.cer -keystore cacerts.jks -keypass changeit
```
