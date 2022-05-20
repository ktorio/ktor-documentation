# Client bearer authentication (OAuth)

A sample project demonstrating how to use bearer [authentication](https://ktor.io/docs/auth.html) to get a Google's profile information. To learn more about OAuth support for accessing Google APIs, see [Using OAuth 2.0 to Access Google APIs](https://developers.google.com/identity/protocols/oauth2).
 
## Obtaining client credentials
To obtain client credentials required for accessing Google APIs, follow the steps below:
1. Create a Google account.
1. Open the [Google Cloud Console](https://console.cloud.google.com/apis/credentials) and create the `OAuth client ID` credentials with the `Android` application type.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
Before running this sample, assign a client ID to the `GOOGLE_CLIENT_ID` environment variable:
```shell
# macOS/Linux
export GOOGLE_CLIENT_ID=yourClientId

# Windows
setx GOOGLE_CLIENT_ID yourClientId
```

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-auth-oauth-google:run -q --console=plain
```

Then, perform the following steps:
1. Open the authorization link displayed in a program's output.
   ```Bash
   10:00:00: Executing task ' run --console=plain --quiet'...

   https://accounts.google.com/o/oauth2/auth?client_id=21966501804...
   ```
2. Log in to your Google account in a browser.
3. Copy the authorization code, return to the terminal, and paste the code below.
4. Press Enter to get a personal greeting.
