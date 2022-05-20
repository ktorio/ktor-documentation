# Google App Engine Standard

A sample Ktor project that can be deployed on a [Google App Engine](https://ktor.io/docs/google-app-engine.html) standard infrastructure.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample locally, execute the following command in a repository's root directory:

```
./gradlew :google-appengine-standard:run
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.  

## Deploying

To deploy this project on Google App Engine, make sure that [these prerequisites](https://ktor.io/docs/google-app-engine.html#prerequisites) are met and then follow the steps below:
1. Execute the following command to create a Google Cloud project:
   ```Bash
   gcloud projects create <unique-project-id> --set-as-default
   ```
   Replace `<unique-project-id>` with the desired project ID.
2. Create an App Engine application for your Cloud project:
   ```Bash
   gcloud app create
   ```
3. To deploy the application, run the `appengineDeploy` task:
   ```Bash
   ./gradlew :google-appengine-standard:appengineDeploy
   ```
