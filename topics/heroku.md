[//]: # (title: Heroku)

<include src="lib.md" include-id="outdated_warning"/>

>There is a quickstart repository for Heroku: <https://github.com/orangy/ktor-heroku-start>
>
{type="note"}

## Preparing

For using Heroku, you will need Java, Maven/Gradle and the [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli)

You will also need to configure your public key in the Heroku configuration.

You can try the `heroku --version` command to see if you have the command line installed:

```text
> heroku --version
heroku-cli/6.15.36 (darwin-x64) node-v9.9.0
```

You will also need an `app.json` file describing your projects and your dependencies:
```json
{
  "name": "Start on Heroku: Kotlin",
  "description": "A barebones Kotlin app, which can easily be deployed to Heroku.",
  "image": "heroku/java",
  "addons": [ "heroku-postgresql" ]
}
```

You will also need a `Procfile` describing what to execute:

```text
web:    java -jar target/helloworld.jar
```

And a `system.properties` file describing your java version:

```text
java.runtime.version=1.8
```

## Running locally

And a file called `.env` along with the other files(required for development).
This will contain environment variables that Heroku will pass to the application.
For example, for the quickstart:

```properties
PORT=8080
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/java_database_name
```

If your local installation of postgresql has a user/password, you have to change the jdbc url too:
```properties
JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/java_database_name?user=user&password=password
```

You will also first need to create the database:

```text
> psql -c "CREATE DATABASE java_database_name;"

CREATE DATABASE
```

With these files, you can use Gradle or Maven to create a [fat-jar](fatjar.md) and adjust the `Procfile`
to point to the right file.

After building the jar, in Unix systems you can use `heroku local:start` to start your server.

## Deploying

You first have to create an app or set the git remote. `heroku create` will create an app
with a random available name and it will set a git remote of the repo.
After calling `heroku create`, you should see something like this:

```text
> heroku create
Creating app... done, ⬢ demo-demo-12345
https://demo-demo-12345.herokuapp.com/ | https://git.heroku.com/demo-demo-12345.git
```

This effectively adds a `heroku` remote to your git clone:

```text
> cat .git/config
...
[remote "heroku"]
	url = https://git.heroku.com/demo-demo-12345.git
	fetch = +refs/heads/*:refs/remotes/heroku/*
```

After that, you have to push your git changes to the `heroku` remote. And it does a build on push:

```text
> git push heroku master
Counting objects: 90, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (59/59), done.
Writing objects: 100% (90/90), 183.08 KiB | 5.55 MiB/s, done.
Total 90 (delta 21), reused 0 (delta 0)
remote: Compressing source files... done.
remote: Building source:
remote:
remote: -----> Java app detected
remote: -----> Installing JDK 1.8... done
remote: -----> Executing: ./mvnw -DskipTests clean dependency:list install
...
remote:        [INFO] BUILD SUCCESS
remote:        [INFO] ------------------------------------------------------------------------
remote:        [INFO] Total time: 49.698 s
remote:        [INFO] Finished at: 2018-03-23T04:33:01+00:00
remote:        [INFO] Final Memory: 41M/399M
remote:        [INFO] ------------------------------------------------------------------------
remote: -----> Discovering process types
remote:        Procfile declares types -> web
remote:
remote: -----> Compressing...
remote:        Done: 60.7M
remote: -----> Launching...
remote:        Released v4
remote:        https://demo-demo-12345.herokuapp.com/ deployed to Heroku
remote:
remote: Verifying deploy... done.
To https://git.heroku.com/demo-demo-12345.git
 * [new branch]      master -> master
```

Now you can execute `heroku open` to open your application in your browser:

```text
heroku open
```

In this case, it will open: https://demo-demo-12345.herokuapp.com/

If you encountered:

```text
remote:  !     ERROR: Failed to run Gradle!
remote:        It looks like your project does not contain a 'stage' task, which Heroku needs in order
remote:        to build your app.
```
It probably means that Heroku doesn't know how to build your app and you have to set it manually. For example, if you used [shadowJar](https://github.com/johnrengelman/shadow) you have to set it using:

```text
> heroku config:set GRADLE_TASK="shadowJar"
Setting GRADLE_TASK and restarting ⬢ demo-demo-12345... done, v4
GRADLE_TASK: shadowJar
```

>Remember that Heroku sets an environment variable called `PORT` which you have to bind to instead of
>a fixed port.
>When using embeddedServer you will have to use `System.getenv`, while when using `application.conf` you will
>have to set `ktor.deployment.port = ${PORT}`.
>Check out the page about
>[using environment variables in the configuration](Configurations.md#environment-variables)
>for more information.
>
{type="note"}