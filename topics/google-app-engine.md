[//]: # (title: Google App Engine)

<microformat>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-gradle-sample/">ktor-gradle-sample</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/google-appengine-standard">google-appengine-standard</a>
</p>
</microformat>

In this tutorial, we'll show you how to prepare and deploy a Ktor project to a Google App Engine standard environment. This tutorial uses a Ktor application created in the [](Gradle.xml) topic.



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
1. Clone the [ktor-gradle-sample](https://github.com/ktorio/ktor-gradle-sample) project.
2. Switch a branch from `main` to one of the following:
   ```Bash
   git checkout embedded-server # a server is configured in code
   # or
   git checkout engine-main # a server is configured in 'application.conf'
   ```
   {style="block"}
   These branches demonstrate different approaches to [creating and configuring a Ktor server](create_server.xml): in code or by using the `application.conf` configuration file. In this tutorial, deploying process is the same for both approaches.

## Prepare an application {id="prepare-app"}
### Apply the Shadow plugin {id="configure-shadow-plugin"}
This tutorial shows how to deploy the application to Google App Engine using a [fat JAR](fatjar.md). To generate fat JARs, you need to apply the Shadow plugin by. Open the `build.gradle` file and add the plugin to the `plugins` block:
```groovy
```
{src="snippets/google-appengine-standard/build.gradle" lines="1,3,6"}


### Configure the App Engine plugin {id="configure-app-engine-plugin"}
The [Google App Engine Gradle plugin](https://github.com/GoogleCloudPlatform/app-gradle-plugin) provides tasks to build and deploy Google App Engine applications. To use this plugin, follow the steps below:

1. Open the `settings.gradle` and insert the following code to reference a plugin from the Central Maven repository:
   ```groovy
   ```
   {src="settings.gradle" lines="1-13"}

3. Open `build.gradle` and apply the plugin in the `plugins` block:
   ```groovy
   ```
   {src="snippets/google-appengine-standard/build.gradle" lines="1-2,6"}

4. Add the `appengine` block with the following settings in the `build.gradle` file:
   ```groovy
   ```
   {src="snippets/google-appengine-standard/build.gradle" lines="16-24"}


### Configure App Engine settings {id="configure-app-engine-settings"}
You configure App Engine settings for your application in the [app.yaml](https://cloud.google.com/appengine/docs/standard/python/config/appref):
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
   
2. Create an App Engine application for the created Cloud project:
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

You can find the completed example here: [google-appengine-standard](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/google-appengine-standard).
