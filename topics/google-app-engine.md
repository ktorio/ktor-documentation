[//]: # (title: Google App Engine)

<include src="lib.md" include-id="outdated_warning"/>

>You can check out a full google appengine sample, here:
><https://github.com/ktorio/ktor-samples/tree/1.3.0/deployment/google-appengine-standard>
>
{type="note"}

## Preparing

You first need to install the `gcloud` cli. You can grab it from here:
<https://cloud.google.com/sdk/docs/> and follow the described steps to install it.

For example, a macOS setup might look something like this:

```text
> wget https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-194.0.0-darwin-x86_64.tar.gz
> tar -xzf google-cloud-sdk-194.0.0-darwin-x86_64.tar.gz
> cd google-cloud-sdk

> ./install.sh
Welcome to the Google Cloud SDK!

To help improve the quality of this product, we collect anonymized usage data
and anonymized stacktraces when crashes are encountered; additional information
is available at <https://cloud.google.com/sdk/usage-statistics>. You may choose
to opt out of this collection now (by choosing 'N' at the below prompt), or at
any time in the future by running the following command:

    gcloud config set disable_usage_reporting true

Do you want to help improve the Google Cloud SDK (Y/n)?  n

Your current Cloud SDK version is: 194.0.0
The latest available version is: 194.0.0

┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                                  Components                                                 │
├───────────────┬──────────────────────────────────────────────────────┬──────────────────────────┬───────────┤
│     Status    │                         Name                         │            ID            │    Size   │
├───────────────┼──────────────────────────────────────────────────────┼──────────────────────────┼───────────┤
│ Not Installed │ App Engine Go Extensions                             │ app-engine-go            │ 151.3 MiB │
│ Not Installed │ Cloud Bigtable Command Line Tool                     │ cbt                      │   4.0 MiB │
│ Not Installed │ Cloud Bigtable Emulator                              │ bigtable                 │   3.8 MiB │
│ Not Installed │ Cloud Datalab Command Line Tool                      │ datalab                  │   < 1 MiB │
│ Not Installed │ Cloud Datastore Emulator                             │ cloud-datastore-emulator │  17.9 MiB │
│ Not Installed │ Cloud Datastore Emulator (Legacy)                    │ gcd-emulator             │  38.1 MiB │
│ Not Installed │ Cloud Pub/Sub Emulator                               │ pubsub-emulator          │  33.4 MiB │
│ Not Installed │ Emulator Reverse Proxy                               │ emulator-reverse-proxy   │  14.5 MiB │
│ Not Installed │ Google Container Local Builder                       │ container-builder-local  │   3.7 MiB │
│ Not Installed │ Google Container Registry's Docker credential helper │ docker-credential-gcr    │   2.5 MiB │
│ Not Installed │ gcloud Alpha Commands                                │ alpha                    │   < 1 MiB │
│ Not Installed │ gcloud Beta Commands                                 │ beta                     │   < 1 MiB │
│ Not Installed │ gcloud app Java Extensions                           │ app-engine-java          │ 118.9 MiB │
│ Not Installed │ gcloud app PHP Extensions                            │ app-engine-php           │  21.9 MiB │
│ Not Installed │ gcloud app Python Extensions                         │ app-engine-python        │   6.2 MiB │
│ Not Installed │ gcloud app Python Extensions (Extra Libraries)       │ app-engine-python-extras │  27.8 MiB │
│ Not Installed │ kubectl                                              │ kubectl                  │  12.2 MiB │
│ Installed     │ BigQuery Command Line Tool                           │ bq                       │   < 1 MiB │
│ Installed     │ Cloud SDK Core Libraries                             │ core                     │   7.4 MiB │
│ Installed     │ Cloud Storage Command Line Tool                      │ gsutil                   │   3.4 MiB │
└───────────────┴──────────────────────────────────────────────────────┴──────────────────────────┴───────────┘
To install or remove components at your current SDK version [194.0.0], run:
  $ gcloud components install COMPONENT_ID
  $ gcloud components remove COMPONENT_ID

To update your SDK installation to the latest version [194.0.0], run:
  $ gcloud components update

Modify profile to update your $PATH and enable shell command
completion?

Do you want to continue (Y/n)?  Y

The Google Cloud SDK installer will now prompt you to update an rc
file to bring the Google Cloud CLIs into your environment.

Enter a path to an rc file to update, or leave blank to use
[/Users/user/.zshrc]:
Backing up [/Users/user/.zshrc] to [/Users/user/.zshrc.backup].
[/Users/user/.zshrc] has been updated.

==> Start a new shell for the changes to take effect.

For more information on how to get started, please visit:
  https://cloud.google.com/sdk/docs/quickstarts
```

After that, you can start a new shell, and you should have access to the `gcloud` cli. For example:

```text
> gcloud --version
Google Cloud SDK 194.0.0
bq 2.0.30
core 2018.03.16
gsutil 4.29
```

You will also need to install some components with the cli (`gcloud components install app-engine-java`):

```text
> gcloud components install app-engine-java

Your current Cloud SDK version is: 194.0.0
Installing components from version: 194.0.0

┌────────────────────────────────────────────────────┐
│        These components will be installed.         │
├──────────────────────────────┬─────────┬───────────┤
│             Name             │ Version │    Size   │
├──────────────────────────────┼─────────┼───────────┤
│ gRPC python library          │         │           │
│ gRPC python library          │   1.9.1 │   7.6 MiB │
│ gcloud app Java Extensions   │  1.9.63 │ 118.9 MiB │
│ gcloud app Python Extensions │  1.9.67 │   6.2 MiB │
└──────────────────────────────┴─────────┴───────────┘

For the latest full release notes, please visit:
  https://cloud.google.com/sdk/release_notes

Do you want to continue (Y/n)?  Y

╔════════════════════════════════════════════════════════════╗
╠═ Creating update staging area                             ═╣
╠════════════════════════════════════════════════════════════╣
╠═ Installing: gRPC python library                          ═╣
╠════════════════════════════════════════════════════════════╣
╠═ Installing: gRPC python library                          ═╣
╠════════════════════════════════════════════════════════════╣
╠═ Installing: gcloud app Java Extensions                   ═╣
╠════════════════════════════════════════════════════════════╣
╠═ Installing: gcloud app Python Extensions                 ═╣
╠════════════════════════════════════════════════════════════╣
╠═ Creating a backup and activating a new installation      ═╣
╚════════════════════════════════════════════════════════════╝

Performing post-processing steps...done.

Update done!
```
{ .compact }

For your project, you can use gradle and the official `appengine-gradle-plugin`. So a `build.gradle` would look like this:


```groovy
buildscript {
    ext.appengine_version = '1.9.60'
    ext.appengine_plugin_version = '1.3.4'

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "com.google.cloud.tools:appengine-gradle-plugin:$appengine_plugin_version"
    }
}

apply plugin: 'kotlin'
apply plugin: 'war'
apply plugin: 'com.google.cloud.tools.appengine'

// appengine does not honor this property, so we are forced to use deep Maven tree layout
// webAppDirName = file('webapp')

sourceSets {
    main.kotlin.srcDirs = [ 'src/main/kotlin' ]
}

repositories {
    mavenCentral()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    compile "io.ktor:ktor-server-servlet:$ktor_version"
    compile "io.ktor:ktor-html-builder:$ktor_version"
    compile "org.slf4j:slf4j-jdk14:$slf4j_version"

    providedCompile "com.google.appengine:appengine:$appengine_version"
}

kotlin.experimental.coroutines = 'enable'

task run(dependsOn: appengineRun)
```

Once everything is configured, you can now run the application locally, using the gradle task `appengineRun`:

In this case, these commands are executed in the root of the ktor-samples repository <https://github.com/ktorio/ktor-samples/>:  

```text
./gradlew :google-appengine-standard:appengineRun
```

It should start the server in <http://localhost:8080/> and the admin in <http://localhost:8080/_ah/admin>.

## Deploying

First, we need to create a project `gcloud projects create demo-demo-123456 --set-as-default`:

```text
> gcloud projects create demo-demo-123456 --set-as-default
Create in progress for [https://cloudresourcemanager.googleapis.com/v1/projects/demo-demo-123456].
Waiting for [operations/pc.7618150612308930095] to finish...done.
Updated property [core/project] to [demo-demo-123456].
```

And then we need to create an application using `gcloud app create`:

```text
> gcloud app create
You are creating an app for the project [demo-demo-123456].
WARNING: Creating an App Engine application for a project is irreversible, and the region
cannot be changed. More information about regions is at
<https://cloud.google.com/appengine/docs/locations>.

Please choose the region where you want your App Engine application
located:

 [1] europe-west2  (supports standard and flexible)
 [2] us-central    (supports standard and flexible)
 [3] europe-west   (supports standard and flexible)
 [4] europe-west3  (supports standard and flexible)
 [5] us-east1      (supports standard and flexible)
 [6] us-east4      (supports standard and flexible)
 [7] asia-northeast1 (supports standard and flexible)
 [8] asia-south1   (supports standard and flexible)
 [9] australia-southeast1 (supports standard and flexible)
 [10] southamerica-east1 (supports standard and flexible)
 [11] northamerica-northeast1 (supports standard and flexible)
 [12] cancel
Please enter your numeric choice:  1

Creating App Engine application in project [demo-demo-123456] and region [europe-west2]....done.
Success! The app is now created. Please use `gcloud app deploy` to deploy your first app.
```

Now we can deploy the application using `gradle appengineDeploy`:

```text
> gradle :google-appengine-standard:appengineDeploy
Starting a Gradle Daemon (subsequent builds will be faster)
Reading application configuration data...
Mar 23, 2018 6:32:09 AM com.google.apphosting.utils.config.IndexesXmlReader readConfigXml
INFORMATION: Successfully processed /Users/user/projects/ktor-samples/deployment/google-appengine-standard/build/exploded-google-appengine-standard/WEB-INF/appengine-generated/datastore-indexes-auto.xml

Beginning interaction for module default...
0% Scanning for jsp files.
0% Generated git repository information file.
Success.
Temporary staging for module default directory left in /Users/user/projects/ktor-samples/deployment/google-appengine-standard/build/staged-app
Services to deploy:

descriptor:      [/Users/user/projects/ktor-samples/deployment/google-appengine-standard/build/staged-app/app.yaml]
source:          [/Users/user/projects/ktor-samples/deployment/google-appengine-standard/build/staged-app]
target project:  [demo-demo-123456]
target service:  [default]
target version:  [20180323t063212]
target url:      [https://demo-demo-123456.appspot.com]

Beginning deployment of service [default]...
Some files were skipped. Pass `--verbosity=info` to see which ones.
You may also view the gcloud log file, found at
[/Users/user/.config/gcloud/logs/2018.03.23/06.32.10.739209.log].
#============================================================#
#= Uploading 38 files to Google Cloud Storage               =#
#============================================================#
File upload done.
Updating service [default]...
..............done.
Setting traffic split for service [default]...
.......done.
Deployed service [default] to [https://demo-demo-123456.appspot.com]

You can stream logs from the command line by running:
  $ gcloud app logs tail -s default

To view your application in the web browser run:
  $ gcloud app browse

BUILD SUCCESSFUL in 42s
6 actionable tasks: 2 executed, 4 up-to-date
```
{ .compact }

Now you can view your application in your browser with `gcloud app browse`. It will open
the application. In this case: https://demo-demo-123456.appspot.com