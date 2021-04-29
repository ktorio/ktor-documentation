# OAuth Google authentication
A sample project demonstrating how to use [OAuth](https://ktor.io/docs/oauth.html) to get a Google's profile information. To learn more about OAuth support for accessing Google APIs, see [Using OAuth 2.0 to Access Google APIs](https://developers.google.com/identity/protocols/oauth2).

## Obtaining client credentials
To obtain client credentials required for accessing Google APIs, follow the steps below:
1. Create a Google account.
1. Obtain a client ID and client secret from the [Google API Console](https://console.developers.google.com/).
1. Assign a client ID and client secret to the `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET` environment variables:
   ```shell
   # macOS/Linux
   export GOOGLE_CLIENT_ID=yourClientId
   export GOOGLE_CLIENT_SECRET=yourClientSecret
   
   # Windows
   setx GOOGLE_CLIENT_ID yourClientId
   setx GOOGLE_CLIENT_SECRET yourClientSecret
   ```

## Running
To run this sample, execute the following command in a repository's root directory:
```shell
# macOS/Linux
./gradlew :auth-oauth-google:run

# Windows
gradlew.bat :auth-oauth-google:run
```

Then, open the [http://localhost:8080/](http://localhost:8080/) page and enter credentials for your Google account to get a personal greeting.