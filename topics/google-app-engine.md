[//]: # (title: Deploy a Ktor app to Google App Engine)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main">engine-main</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/google-appengine-standard">google-appengine-standard</a>
</p>
</tldr>

<web-summary>
This tutorial shows how to prepare and deploy a Ktor project to a Google App Engine standard environment.
</web-summary>

<link-summary>
Learn how to deploy your project to a Google App Engine standard environment.
</link-summary>

In this tutorial, you will learn how to prepare and deploy a Ktor project to a Google App Engine standard environment. 
The tutorial uses the [engine-main](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main) sample project as a starting point.

## Prerequisites {id="prerequisites"}

Before starting this tutorial, complete the steps below:
* Register on [Google Cloud Platform](https://console.cloud.google.com/).
* Install and initialize the [Google Cloud SDK](https://cloud.google.com/sdk/docs/install).
* Install the App Engine extension for Java with the following command:
   ```Bash
   gcloud components install app-engine-java
   ```

## Clone a sample application {id="clone"}

To open a sample application, follow the steps below:

1. Clone the Ktor documentation repository and open the [`codeSnippets`](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets) project.
2. Open the [engine-main](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main) module.
   > Ktor provides two approaches for [creating and configuring a server](server-create-and-configure.topic): in code or in a configuration
   > file. In this tutorial, the deploying process is the same for both approaches.
   >
   {style="note"}

## Prepare an application {id="prepare-app"}

### Step 1: Apply the Shadow plugin {id="configure-shadow-plugin"}

This tutorial shows how to deploy your application to Google App Engine using a [fat JAR](server-fatjar.md). To generate fat JARs,
you must apply the Shadow plugin.

First, open your <path>gradle/libs.versions.toml</path> file and define the Shadow plugin version and its alias:

```toml
```
{src="gradle/libs.versions.toml" include-lines="1,28,68,74"}

Then, open the <path>build.gradle.kts</path> file and add the plugin to the `plugins` block:
```kotlin
```
{src="snippets/google-appengine-standard/build.gradle.kts" include-lines="4,8-9"}


### Step 2: Configure the App Engine plugin {id="configure-app-engine-plugin"}

The [Google App Engine Gradle plugin](https://github.com/GoogleCloudPlatform/app-gradle-plugin) provides tasks to build and deploy Google App Engine applications. To use
this plugin, follow the steps below:

1. Open the <path>settings.gradle.kt</path> file and use the following code to reference a plugin from the Central Maven repository:
   ```kotlin
   ```
   {src="settings.gradle.kts" include-lines="3-16"}

2. Open the <path>build.gradle.kts</path> file and apply the plugin in the `plugins` block:
   ```kotlin
   ```
   {src="snippets/google-appengine-standard/build.gradle.kts" include-lines="4,7,9"}

3. In the same <path>build.gradle.kts</path> file, add the `appengine` block with the following settings:
   ```kotlin
   ```
   {src="snippets/google-appengine-standard/build.gradle.kts" include-lines="1,20-28"}


### Step 3: Configure App Engine settings {id="configure-app-engine-settings"}

You configure App Engine settings for your application in the [<path>app.yaml</path>](https://cloud.google.com/appengine/docs/standard/python/config/appref) file:
1. Create the <path>appengine</path> directory inside <path>src/main</path>.
2. Inside this directory, create the <path>app.yaml</path> file and add the following content:
   ```yaml
   ```
   {src="snippets/google-appengine-standard/src/main/appengine/app.yaml"}

   Replace `google-appengine-standard` with your project's name.

   The `entrypoint` option contains a command used to run a fat JAR generated for the application.

   > For more information on supported configuration options, see the
   > [Google AppEngine documentation](https://cloud.google.com/appengine/docs/standard/reference/app-yaml?tab=java).
   >
   {style="tip"}

## Deploy an application {id="deploy-app"}

To deploy your application, open a new terminal window and follow the steps below:

1. First, create a Google Cloud project, which is a top-level container holding application resources. For example, the command below creates a project with the `ktor-sample-app-engine` name:
   ```Bash
   gcloud projects create ktor-sample-app-engine --set-as-default
   ```
   
2. Create an App Engine application for the Cloud project:
   ```Bash
   gcloud app create
   ```

3. To deploy the application, run the `appengineDeploy` Gradle task:
   ```Bash
   ./gradlew appengineDeploy
   ```
4. Wait until Google Cloud builds and publishes the application:
   ```
   ...done.
   Deployed service [default] to [https://ktor-sample-app-engine.ew.r.appspot.com]
   ```
   {style="block"}
   > If you receive `Cloud Build has not been used in project` error during the build, enable it by using instructions from an error report.
   >
   {style="note"}

   > For the complete code example, see [google-appengine-standard](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/google-appengine-standard).
   > 
   {style="tip"}
