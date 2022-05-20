# LDAP authentication
A sample project demonstrating how to use [LDAP authentication](https://ktor.io/docs/ldap.html) in Ktor. This project includes Docker configuration for running a test [OpenLDAP](https://github.com/osixia/docker-openldap) server populated with sample data.

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running an LDAP server
To run an LDAP server, execute the following command in a project directory:
```bash
docker-compose up
```
Wait until Docker Compose pulls/builds the images and starts containers.

## Running an application
To run this sample, execute the following command in a repository's root directory:
```bash
# macOS/Linux
./gradlew :auth-ldap:run

# Windows
.\gradlew.bat :auth-ldap:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) and enter the `jetbrains`/`foobar` credentials in a login dialog to see a web page.