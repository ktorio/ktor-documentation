# Integrate a database

A sample project showing how to integrate a
database intro a Ktor application using Exposed and PostgreSQL by following
the tutorial [Integrate a Database](https://ktor.io/docs/server-integrate-database.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Install PostgreSQL

For this example to run, you need to have a running PostgreSQL database with a populated table.

To learn how to install PostgreSQL, see the [official documentation](https://www.postgresql.org/docs/current/).

To create the `Task` table, follow the instructions
in [the tutorial](https://ktor.io/docs/server-integrate-database.html#create-schema).

## Setup the database connection

1. Navigate to the `Databases.kt` file in `src/main/kotlin/com/example/plugins/`.
2. Replace the attributes in the `Database.connect()` function to correspond to your set-up:
```kotlin
fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
        user = "postgresql",
        password = "password"
    )
}
```
## Run

To run the application, execute the following command from the `codeSnippets` directory:

```bash
./gradlew :tutorial-server-db-integration:run
```

