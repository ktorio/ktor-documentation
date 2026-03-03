# Integrate a database

A standalone sample project showing how to integrate a
database intro a Ktor application using Exposed and PostgreSQL by following
the tutorial [Integrate a database](https://ktor.io/docs/server-integrate-database.html).

## Install PostgreSQL

For this example to run, you need to have a running PostgreSQL database with a populated table.

To learn how to install PostgreSQL, see the [official documentation](https://www.postgresql.org/docs/current/).

To create the `Task` table, follow the instructions
in [the tutorial](https://ktor.io/docs/server-integrate-database.html#create-schema).

## Set up the database connection

1. Navigate to `src/main/kotlin/` and open the `Databases.kt` file.
2. Replace the attributes in the `Database.connect()` function to correspond to your setup:
```kotlin
fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
        user = "postgresql",
        password = "password"
    )
}
```

## Build

To build the application, run the following command from the project's root directory:

```bash
./gradlew build
```


## Run

To run the application, run the following command from the project's root directory:

```bash
./gradlew run
```

The application will be running at [http://0.0.0.0:8080/static/index.html](http://0.0.0.0:8080/static/index.html).
