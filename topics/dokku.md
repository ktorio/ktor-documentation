[//]: # (title: Dokku)

<show-structure for="chapter" depth="2"/>

<link-summary>Learn how to prepare and deploy a Ktor application to Dokku.</link-summary>

[Dokku](https://dokku.com/) is a self-hosted Platform-as-a-Service (PaaS) that runs on your own Linux server and provides a deployment workflow similar to [Heroku](heroku.md). While you can deploy a Ktor application by copying a fat JAR to a server manually, Dokku automates the surrounding infrastructure:

* **Git-based deployments** — push your code with `git push` and Dokku builds and restarts the application automatically, without SSH file copying or manual restarts.
* **Process management** — Dokku starts, stops, and restarts your application automatically, including after server reboots.
* **Multiple apps on one server** — each application runs in an isolated container, preventing port conflicts and interference between apps.
* **HTTPS** — a single command provisions a Let's Encrypt certificate for your app.
* **Zero-downtime deployments** — Dokku waits for the new container to pass a health check before switching traffic away from the old one.

Dokku requires a Linux server to run on. Several hosting providers offer pre-installed Dokku images, so you don't need to set it up manually: [DigitalOcean](https://marketplace.digitalocean.com/apps/dokku), [Hostinger](https://www.hostinger.com/vps/dokku-hosting), and [HOSTKEY](https://hostkey.com/apps/developer-tools/dokku/).

## Prerequisites {id="prerequisites"}
Before starting this tutorial, make sure that the following prerequisites are met:
* You have a Linux server with Dokku installed. You can [install it manually](https://dokku.com/docs/getting-started/installation/) or use a hosting provider that offers a pre-installed Dokku image.
* [Git](https://git-scm.com/downloads) is installed on your machine.


## Prepare an application {id="prepare-app"}

### Step 1: Configure a port {id="port"}

First, you need to specify a port used to listen for incoming requests. Dokku dynamically assigns a port to each application and passes it using the `PORT` environment variable. Your application must read this variable at startup, otherwise it will listen on the wrong port and Dokku won't be able to route traffic to it. Depending on the way used to [configure a Ktor server](server-create-and-configure.topic), do one of the following:
* If your server configuration is specified in code, read the environment variable using the `System.getenv()` function and pass it to the `port` parameter of the `embeddedServer()` function:
   ```kotlin
   fun main() {
       embeddedServer(Netty, port = System.getenv("PORT")?.toIntOrNull() ?: 8080) {
           // ...
       }.start(wait = true)
   }
   ```

* If your server configuration is specified in a configuration file, open your <path>application.conf</path> or <path>application.yaml</path> placed in `src/main/resources` and update the `port` property as shown below:

   <tabs group="config">
   <tab title="application.conf" group-key="hocon">

   ```shell
   ktor {
       deployment {
           port = 8080
           port = ${?PORT}
       }
   }
   ```

   </tab>
   <tab title="application.yaml" group-key="yaml">

   ```yaml
   ktor:
       deployment:
           port: ${PORT:8080}
   ```

   </tab>
   </tabs>

### Step 2: Add a stage task {id="stage"}

Open the <path>build.gradle.kts</path> file and add a custom `stage` task that Dokku uses to build the application:
```kotlin
tasks {
    register("stage").configure {
        dependsOn("installDist")
    }
}
```
> The `installDist` task comes with the Gradle [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html).
>{style="tip"}

### Step 3: Specify the Java version {id="java-version"}

Create a <path>system.properties</path> file in the project root to specify the Java version:
```properties
java.runtime.version=21
```

The version must match the JVM toolchain version specified in your <path>build.gradle.kts</path> file. Without this file, Dokku will use the latest available JDK version, which may change over time and cause unexpected build failures.

### Step 4: Create a Procfile {id="procfile"}

Create a `Procfile` in the project root and add the following content:
```text
web: ./build/install/<project-name>/bin/<project-name>
```
{style="block"}

This file tells Dokku how to start the application after it is built by the [stage](#stage) task.
Replace `<project-name>` with your project name, which you can find by running:
```bash
./gradlew properties -q | grep "^name:" | sed 's/name: //'
```

## Deploy an application {id="deploy-app"}

To deploy the application to Dokku using Git, open the terminal and follow the steps below:

1. Commit changes made in the [previous section](#prepare-app) locally:
   ```bash
   git add .
   git commit -m "Prepare app for deploying"
   ```
2. SSH into your server and create a Dokku application.
   Replace `<app-name>` with a name for your application:
   ```bash
   ssh <user>@<your-server> dokku apps:create <app-name>
   ```
3. Add the Dokku server as a Git remote.
   Replace `<your-server>` with your server's hostname or IP address, and `<app-name>` with the name used in the previous step:
   ```bash
   git remote add dokku dokku@<your-server>:<app-name>
   ```
4. Push the code to Dokku to trigger a build and deployment:
   ```bash
   git push dokku main
   ```
   > Replace `main` with your branch name if it differs.

   > If your Ktor application is in a subdirectory of the repository, use `git subtree push` instead:
   > ```bash
   > git subtree push --prefix=<subdir> dokku main
   > ```
   Wait until Dokku builds and starts the application:
   ```text
   ...
   =====> Application deployed:
          http://<app-name>.<your-server>
   ```
   {style="block"}
5. Set a domain or IP address to make the application accessible:
   ```bash
   ssh <user>@<your-server> dokku domains:set <app-name> <domain-or-ip>
   ```
   The application will be available at `http://<domain-or-ip>`.
