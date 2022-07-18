[//]: # (title: Google App Engine)

<tldr>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/engine-main">engine-main</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/google-appengine-standard">google-appengine-standard</a>
</p>
</tldr>

<link-summary>
This tutorial shows how to prepare and deploy a Ktor project to a Google App Engine standard environment.
</link-summary>

In this tutorial, we'll show you how to prepare and deploy a Ktor project to a Google App Engine standard environment. This tutorial uses the [engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/engine-main) sample project as a starting project.



## Prerequisites {id="prerequisites"}
Before starting this tutorial, you need to perform the steps below:
* Register in [Google Cloud Platform](https://console.cloud.google.com/).
* Install and initialize the [Google Cloud SDK](https://cloud.google.com/sdk/docs/install).
* Install the App Engine extension for Java with the following command:
   ```Bash
   gcloud components install app-engine-java
   ```

## Clone a sample application {id="clone"}
To open a sample application, follow the steps below:
1. Clone a Ktor documentation repository and open the [codeSnippets](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets) project.
2. Open the [engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/engine-main) module.
   > Note that Ktor provides two approaches to [create and configure a server](create_server.xml): in code or by using a configuration file. In this tutorial, deploying process is the same for both approaches.

## Prepare an application {id="prepare-app"}
### Step 1: Apply the Shadow plugin {id="configure-shadow-plugin"}
This tutorial shows how to deploy the application to Google App Engine using a [fat JAR](fatjar.md). To generate fat JARs, you need to apply the Shadow plugin. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:
```kotlin
```
{src="snippets/google-appengine-standard/build.gradle.kts" lines="7,11-12"}


### Step 2: Configure the App Engine plugin {id="configure-app-engine-plugin"}
The [Google App Engine Gradle plugin](https://github.com/GoogleCloudPlatform/app-gradle-plugin) provides tasks to build and deploy Google App Engine applications. To use this plugin, follow the steps below:

1. Open the `settings.gradle.kts` file and insert the following code to reference a plugin from the Central Maven repository:
   ```groovy
   ```
   {src="settings.gradle.kts" lines="1-14"}

2. Open `build.gradle.kts` and apply the plugin in the `plugins` block:
   ```kotlin
   ```
   {src="snippets/google-appengine-standard/build.gradle.kts" lines="7,10,12"}

3. Add the `appengine` block with the following settings in the `build.gradle.kts` file:
   ```kotlin
   ```
   {src="snippets/google-appengine-standard/build.gradle.kts" lines="1,22-31"}


### Step 3: Configure App Engine settings {id="configure-app-engine-settings"}
You configure App Engine settings for your application in the [app.yaml](https://cloud.google.com/appengine/docs/standard/python/config/appref) file:
1. Create the `appengine` directory inside `src/main/appengine`.
2. Inside this directory, create the `app.yaml` file and add the following content:
   ```yaml
   ```
   {src="snippets/google-appengine-standard/src/main/appengine/app.yaml"}
   
   The `entrypoint` option contains a command used to run a fat JAR generated for the application.


## Deploy an application {id="deploy-app"}

To deploy the application, open the terminal and follow the steps below:

1. First, create a Google Cloud project, which is a top-level container holding application resources. For example, the command below creates a project with the `ktor-sample-app-engine` name:
   ```Bash
   gcloud projects create ktor-sample-app-engine --set-as-default
   ```
   
2. Create an App Engine application for the Cloud project:
   ```Bash
   gcloud app create
   ```

3. To deploy the application, execute the `appengineDeploy` Gradle task...
   ```Bash
   ./gradlew appengineDeploy
   ```
   ... and wait until Google Cloud builds and publishes the application:
   ```
   ...done.
   Deployed service [default] to [https://ktor-sample-app-engine.ew.r.appspot.com]
   ```
   {style="block"}
   > If you receive `Cloud Build has not been used in project` error during the build, enable it by using instructions from an error report.
   >
   {type="note"}

You can find the completed example here: [google-appengine-standard](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/google-appengine-standard).
