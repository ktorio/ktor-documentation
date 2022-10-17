# OAuth Google authentication
A sample project demonstrating how to use [OAuth](https://ktor.io/docs/oauth.html) to get a Google's profile information. To learn more about OAuth support for accessing Google APIs, see [Using OAuth 2.0 to Access Google APIs](https://developers.google.com/identity/protocols/oauth2).
 
## Obtaining client credentials
To obtain client credentials required for accessing Google APIs, follow the steps below:
1. Create a Google account.
2. Open the [Google Cloud Console](https://console.cloud.google.com/apis/credentials) and create the `OAuth client ID` credentials with the `Web application` application type.
3. Make sure that the following settings are specified:
   * **Authorised JavaScript origins**: `http://localhost:8080`
   * **Authorised redirect URIs**: `http://localhost:8080/callback`

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
Before running this sample, assign a client ID and client secret to the `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` environment variables:
```shell
# macOS/Linux
export GOOGLE_CLIENT_ID=yourClientId
export GOOGLE_CLIENT_SECRET=yourClientSecret
   
# Windows
setx GOOGLE_CLIENT_ID yourClientId
setx GOOGLE_CLIENT_SECRET yourClientSecret
```

To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :auth-oauth-google:run
```

Then, open the [http://localhost:8080/](http://localhost:8080/) page and enter credentials for your Google account to get a personal greeting.