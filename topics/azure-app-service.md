[//]: # (title: Azure App Service)

<show-structure for="chapter" depth="2"/>

<link-summary>This tutorial shows how to build, configure and deploy your Ktor application to Azure App Service.</link-summary>

This tutorial shows how to build, configure and deploy your Ktor application to Azure App Service.

## Prerequisites {id="prerequisites"}
Before starting this tutorial, you will need the following:
* An Azure account ([Free trial here](https://azure.microsoft.com/en-us/free/)).
* The [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli) installed in your machine.

## Create a sample application {id="create-sample-app"}

Create a sample application as described in [](server-create-a-new-project.topic). This example will show code and commands based on the following projects: [embedded-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server) and [engine-main](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/engine-main).

> The tutorial above offers two options related to the configuration: either as values directly in the code or by using a configuration file. In either case, the relevant configuration is the port used to listen for incoming requests.

## Set up the application {id="setup-app"}

### Step 1: Set up the port {id="port"}

Here we'll specify the port number where to listen for requests. In Azure App Service, the environment variable `PORT` contains the port number open for incoming requests. Depending on how you created the app in [configure a Ktor server](server-create-and-configure.topic), you'll need to change your code to read this environment variable in one of two places:

* If you used the example with port configuration **in code**, the `PORT` environment variable can be read with `System.getenv` and then parsed to an integer with `.toInt()`. Open the file `Application.kt` and change the port number to the expression shown below:

   ```kotlin
   fun runBasicServer() {
      embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
          // ...
      }.start(wait = true)
   }
    ```
* If the server configuration is defined **in the config file** `application.conf`, update it to read the `PORT` environment variable as in the following example:

   ```
   ktor {
       deployment {
           port = 8080
           port = ${?PORT}
       }
   }
   ```
   {style="block"}

### Step 2: Add plugins {id="plugins"}
Open the `build.gradle.kts` file and add the following lines to the `plugins` section:
```kotlin
plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "3.2.3" // ADDED
    id("com.microsoft.azure.azurewebapp") version "1.10.0" // ADDED
}
```

The `io.ktor.plugin` will provide the task used to create a [fat JAR](server-fatjar.md) and the [Azure WebApp Plugin for Gradle](https://github.com/microsoft/azure-gradle-plugins) will be used to create all the required resources in Azure with ease.

Make sure a `mainClass` is defined in the `application` section, so that there's a well-defined entry point for your fat JAR:

```kotlin
application {
    mainClass.set("com.example.ApplicationKt")
}
```

### Step 3: Configuration {id="configuration"}

If you already created a Java web app in App Service that you want to deploy to, you can skip this step.

Otherwise, add the following entries to `build.gradle.kts` at the end of the file so that the Azure Webapp Plugin creates one for you:

```
 // Rename the fat JAR to the name that the deploy task expects
ktor {
    fatJar {
        archiveFileName.set("embedded-server.jar")
    }
}

// Disable the `jar` task that Azure Plugin would normally run
// to deploy the archive created by the `farJar` task instead
tasks.named("jar") {
    enabled = false
}

// Azure Webapp Plugin configuration
azurewebapp {
  subscription = "YOUR-SUBSCRIPTION-ID"
  resourceGroup = "RESOURCE-GROUP-NAME"
  appName = "WEBAPP-NAME"
  pricingTier = "YOUR-PLAN" // e.g. "F1", "B1", "P0v3", etc.
  region = "YOUR-REGION" // e.g. "westus2"
  setRuntime(closureOf<com.microsoft.azure.gradle.configuration.GradleRuntimeConfig> {
    os("Linux") // Or "Windows"
    webContainer("Java SE")
    javaVersion("Java 21")
  })
  setAuth(closureOf<com.microsoft.azure.gradle.auth.GradleAuthConfig> {
    type = "azure_cli"
  })
}
```

The configuration of most values in the `azurewebapp` section is self-explanatory. For more details see [this document](https://github.com/microsoft/azure-gradle-plugins/wiki/Webapp-Configuration).

* The values for `pricingTier` (Service Plan) can be found here [for Linux](https://azure.microsoft.com/en-us/pricing/details/app-service/linux/) and [for Windows](https://azure.microsoft.com/en-us/pricing/details/app-service/windows/).
* The list of values for `region` can be obtained with the following Azure CLI command: `az account list-locations --query "[].name" --output tsv` or search "App Service" in [this page](https://go.microsoft.com/fwlink/?linkid=2300348&clcid=0x409).

## Build the fat JAR {id="build"}

First, to build the fat JAR, execute the `buildFatJar` task provided by the [Ktor plugin](#plugins) running the command below:

<tabs group="os">
<tab title="Linux/macOS" group-key="unix">
<code-block>./gradlew :embedded-server:buildFatJar</code-block>
</tab>
<tab title="Windows" group-key="windows">
<code-block>gradlew.bat :embedded-server:buildFatJar</code-block>
</tab>
</tabs>

## Deploy the application {id="deploy-app"}

### To a new web app

The authentication method used by the Azure Web App Deploy plugin used in the configuration added earlier will use the Azure CLI. If you haven't done so, log in once with `az login` and follow the instructions.

Finally, deploy the application running the `azureWebAppDeploy` task:

<tabs group="os">
<tab title="Linux/macOS" group-key="unix">
<code-block>./gradlew :embedded-server:azureWebAppDeploy</code-block>
</tab>
<tab title="Windows" group-key="windows">
<code-block>gradlew.bat :embedded-server:azureWebAppDeploy</code-block>
</tab>
</tabs>

This task will take care of the creation of the resource group, plan, web app and finally deploy the fat JAR. When the deployment succeeds, you should see a message like the following:

```
> Task: :embedded-server:azureWebAppDeploy
Auth type: AZURE_CLI
Username: some.username@example.com
Subscription: Some Subscription(13936cf1-cc18-40be-a0d4-177fe532b3dd)
Start creation Resource Group(resource-group) in region (Some Region)
Resource Group (resource-group) is successfully created.
Start creating App Service plan (asp-your-webapp-name)...
App Service plan (asp-your-webapp-name) is successfully created
Start creating Web App(your-webapp-name)...
Web App(your-webapp-name) is successfully created
Trying to deploy artifact to your-webapp-name...
Deploying (C:\docs\ktor-documentation\codeSnippets\snippets\embedded-server\build\libs\embedded-server.jar)[jar] ...
Application url: https://your-webapp-name.azurewebsites.net
```

When the deployment completes, you should be able to see your new web app running in the location shown above.

### To an existing web app

If you had an existing Java web app in Azure App Service, deploy the fat JAR created earlier using the following command of the Azure CLI:

```
az webapp deploy -g RESOURCE-GROUP-NAME -n WEBAPP-NAME --src-path ./path/to/embedded-server.jar --restart true
```

This command will upload the JAR file and restart your web app. After a while, you should see the result of the deployment:

```
Deployment type: jar. To override deployment type, please specify the --type parameter. Possible values: war, jar, ear, zip, startup, script, static
Initiating deployment
Deploying from local path: ./snippets/embedded-server/build/libs/embedded-server-all.jar
Warming up Kudu before deployment.
Warmed up Kudu instance successfully.
Polling the status of sync deployment. Start Time: 2025-09-07 00:07:14.729383+00:00 UTC
Status: Build successful. Time: 5(s)
Status: Starting the site... Time: 23(s)
Status: Starting the site... Time: 41(s)
Status: Site started successfully. Time: 44(s)
Deployment has completed successfully
You can visit your app at: http://your-app-name.some-region.azurewebsites.net
```