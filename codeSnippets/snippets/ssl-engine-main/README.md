# SSL - EngineMain

A sample Ktor project showing how to use [SSL](https://ktor.io/docs/ssl.html) for EngineMain.

## Running

Generate a key:

```Bash
keytool -keystore snippets/ssl-engine-main/test.jks -alias keyAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```
Enter `foobar` as a password.

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :ssl-engine-main:run
```
