# OAuth Google authentication

A sample project demonstrating how to use [OAuth](https://ktor.io/docs/oauth.html) to get Google's profile information.
To learn more about OAuth support for accessing Google APIs,
see [Using OAuth 2.0 to Access Google APIs](https://developers.google.com/identity/protocols/oauth2).

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Obtaining client credentials

To access Google APIs, you need to create authorization credentials in the Google Cloud Console.

1. Open the [Credentials](https://console.cloud.google.com/apis/credentials) page in the Google Cloud Console.
2. Click **CREATE CREDENTIALS** and choose `OAuth client ID`.
3. Choose `Web application` from the dropdown.
4. Specify the following settings:
    * **Authorised JavaScript origins**: `http://localhost:8080`.
    * **Authorised redirect URIs**: `http://localhost:8080/callback`.

   Click **CREATE**.

## Set the environment variables

Before running this sample, assign a client ID and client secret to the `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`
environment variables:

```shell
# macOS/Linux
export GOOGLE_CLIENT_ID=yourClientId
export GOOGLE_CLIENT_SECRET=yourClientSecret
   
# Windows
setx GOOGLE_CLIENT_ID yourClientId
setx GOOGLE_CLIENT_SECRET yourClientSecret
```

You can verify that the environment variables have been correctly set, by using the `echo` command:

```shell
echo $GOOGLE_CLIENT_ID
echo $GOOGLE_CLIENT_SECRET
```

## Run the sample

1. To run this sample, execute the following command in a repository's root directory:

   ```bash
   ./gradlew :auth-oauth-google:run
   ```

2. Open the [http://localhost:8080/](http://localhost:8080/).
3. Click on the "Login with Google" link.
4. Enter your credentials for your Google account.

You should see the following greeting in your browser:
"Hello, YOUR_NAME! Welcome home!"