[//]: # (title: Docker Compose)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-website-interactive-persistence">tutorial-website-interactive-persistence</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-website-interactive-docker-compose">tutorial-website-interactive-docker-compose</a>
</p>
</tldr>

In this topic, we'll show how to run a server Ktor application under Docker Compose. We'll be using a project created in [](interactive_website_add_persistence.md), which uses Exposed to connect to an H2 file database. In this topic, we'll replace H2 with a PostgreSQL database running as a separate `db` service, while the Ktor application will be running as a `web` service.


## Get the application ready {id="prepare-app"}

### Add PostgreSQL dependency {id="add_dependencies"}

First, you need to add dependencies for the PostgreSQL library. Open the `gradle.properties` file and specify library versions:

```kotlin
```
{src="gradle.properties" include-lines="19"}

Then, open `build.gradle.kts` and add the following dependencies:

```kotlin
```
{src="snippets/tutorial-website-interactive-docker-compose/build.gradle.kts" include-lines="4,21-22,29,33"}

### Connect to a database {id="connect_db"}

The [tutorial-website-interactive-persistence](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-website-interactive-persistence) sample uses hardcoded `driverClassName` and `jdbcURL` in the `com/example/dao/DatabaseFactory.kt` file to establish a database connection. Let's extract connection settings for the PostgreSQL database to a [custom configuration group](Configurations.topic#configuration-file). Open the `src/main/resources/application.conf` file and add the `storage` group outside the `ktor` group as follows:

```kotlin
```
{src="snippets/tutorial-website-interactive-docker-compose/src/main/resources/application.conf" include-lines="11-14"}

Note that `jdbcURL` includes the following components:
- `db:5432` is a host and port on which the PostgreSQL database is running.
- `ktorjournal` is the name of the database created when running services.

These settings will be configured later in the [docker-compose.yml](#configure-docker) file.

Open `com/example/dao/DatabaseFactory.kt` and update the `init` function to load storage settings from the configuration file:

```kotlin
```
{src="snippets/tutorial-website-interactive-docker-compose/src/main/kotlin/com/example/dao/DatabaseFactory.kt" include-lines="11-18"}

The `init` function now accepts `ApplicationConfig` and uses `config.property` to load custom settings.

Finally, open `com/example/Application.kt` and pass `environment.config` to `DatabaseFactory.init` to load connection settings on application startup:

```kotlin
```
{src="snippets/tutorial-website-interactive-docker-compose/src/main/kotlin/com/example/Application.kt" include-lines="9-13"}

### Configure the Ktor plugin {id="configure-ktor-plugin"}

In order to run on Docker, the application needs to have all the required files deployed to the container. Depending on the build system you're using,
there are different plugins to accomplish this:
- [](fatjar.md)
- [](maven-assembly-plugin.md)

For example, to apply the Ktor plugin, open the `build.gradle.kts` file and add the `ktor` plugin to the `plugins` block:

```kotlin
```
{src="snippets/tutorial-website-interactive-docker-compose/build.gradle.kts" include-lines="7-11"}


## Configure Docker {id="configure-docker"}

### Prepare Docker image {id="prepare-docker-image"}

To dockerize the application, create the `Dockerfile` in the root of the project and insert the following content:

```dockerfile
```
{src="snippets/tutorial-website-interactive-docker-compose/Dockerfile"}

Note that this `Dockerfile` requires creating a fat JAR before running `docker compose up`. To learn how to use multi-stage builds to generate an application distribution using Docker, see [](docker.md#prepare-docker).

### Configure Docker Compose {id="configure-docker-compose"}

Create the `docker-compose.yml` in the root of the project and add the following content:

```yaml
```
{src="snippets/tutorial-website-interactive-docker-compose/docker-compose.yml"}

- The `web` service is used to run the Ktor application packaged inside the [image](#prepare-docker-image).
- The `db` service uses the `postgres` image to create the `ktorjournal` database for storing articles of our journal.

## Build and run services {id="build-run"}

1. Before running `docker compose up`, create a [fat JAR](#configure-ktor-plugin) containing a Ktor application:
   ```Bash
   ./gradlew :tutorial-website-interactive-docker-compose:buildFatJar
   ```
2. Then, execute `docker compose up` ...
   ```Bash
   docker compose --project-directory snippets/tutorial-website-interactive-docker-compose up
   ```
   ... and wait until Docker Compose pulls/builds the images and starts containers. You can open [`http://localhost:8080/`](http://localhost:8080/) in a browser to create, edit, and delete articles.