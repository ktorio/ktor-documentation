# Tomcat WAR

A sample Ktor project that shows how to configure SSL for the [Tomcat](https://ktor.io/docs/war.html) application server.
In this sample, the SSL connector in configured in [server.xml](server.xml) file.

## Deploying and running

To build and run this application with Docker, execute the following commands:

```bash
./gradlew :tomcat-war-ssl:war
docker compose --project-directory snippets/tomcat-war-ssl up
```

Then, navigate to [https://localhost:8443/tomcat-war-ssl/](https://localhost:8443/tomcat-war-ssl/) to see the sample home page.
