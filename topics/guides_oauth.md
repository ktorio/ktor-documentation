[//]: # (title: Google OAuth)

<include src="lib.md" include-id="outdated_warning"/>


In this guide we are going to implement a login using OAuth.

This is an advanced tutorial and it assumes you have some basic knowledge about Ktor,
so you should follow the [guide about making a Website](website.md) first.





## Creating a host entry pointing to 127.0.0.1

Google's OAuth requires redirect URLs that can't be IP addresses or localhost.
So for development purposes we will need a proper host pointing to 127.0.0.1.
It is not required that this host be accessible from outside our computer, so we can just set up for local host.
There is a public domain <http://lvh.me/> pointing to localhost/127.0.0.1, but you might want to provide your
own host locally for security reasons.

For this, you can add an entry in [the hosts file](https://en.wikipedia.org/wiki/Hosts_(file)) of your machine.

For this guide we are going to associate `me.mydomain.com` to `127.0.0.1`, but you can change it according to your needs,
as long as it is like a public top-level domain (.com, .org...) or has at least two components.

```text
127.0.0.1       me.mydomain.com
```

![](etc_hosts.png){.rounded-shadow}

The structure of this file is simple: <kbd>#</kbd> character for comments,
and each non empty and non-comment line, should contain an IP address followed
by several host names separated by spaces or tabs.

### MacOS/Linux

In MacOS and Linux (Unix) computers, you can find the host file in `/etc/hosts`. You will need root access to edit it.

```text
sudo nano /etc/hosts
```

or

```text
sudo vi /etc/hosts
```

### Windows

In Windows, the host file is held at `%SystemRoot%\System32\drivers\etc\hosts`. You will need admin privileges
to edit this file. For example, you can use Notepad++ opened as administrator.

You can also paste `%SystemRoot%\System32\drivers\etc` in the Windows Explorer the path and then right click
in the hosts file to edit it. The structure of this file is the same as MacOS/Linux.

## Google Developers Console

To be able to use OAuth with any provider, you will need a public `clientId`, and a private `clientSecret`.
In the case of Google login, you can create it using the Google Developers Console:
<https://console.developers.google.com/>{target="_blank"}

First you have to create a new project in the developers console:

![](1.png){.rounded-shadow}
![](2.png){.rounded-shadow}

Inside `API & Services` → `Credentials`, there is a `Create Credentials` button with an `OAuth Client Id` option:

![](3.png){.rounded-shadow}
![](4.png){.rounded-shadow}
![](5.png){.rounded-shadow}
![](6.png){.rounded-shadow}

But first, we have to Configure the OAuth consent screen:

![](7.png){.rounded-shadow}
![](8.png){.rounded-shadow}

Now we can create the OAuth credentials, with the following information:
* **Authorized JavaScript origins:** `http://me.mydomain.com:8080`
* **Authorized redirect URIs:** `http://me.mydomain.com:8080/login`

Press the `Create` button.

![](9.png){.rounded-shadow}

You can change these values later, or add additional authorized URLs by editing the credentials.

You will see a modal dialog with the following:

**OAuth client**
* `Here is your client ID: xxxxxxxxxxx.apps.googleusercontent.com`
* `Here is your client secret: yyyyyyyyyyy`

![](10.png){.rounded-shadow}

## Configuring our application

First we have to define the settings for our OAuth provider. We have to replace the `clientId` and `clientSecret`
with the values obtained from the previous step. Depending on what we need from the user, we can adjust the `defaultScopes`
list to something else. The `profile` scope will have access to the user id, full name, and picture, but not to the email or anything else:

```kotlin
val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "google",
    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
    requestMethod = HttpMethod.Post,

    clientId = "xxxxxxxxxxx.apps.googleusercontent.com",
    clientSecret = "yyyyyyyyyyy",
    defaultScopes = listOf("profile") // no email, but gives full name, picture, and id
)
```

>Remember to adjust the defaultScopes to just request what you really need for the sake of security, user privacy, and trust. 
>
{type="note"}

We also have to install the OAuth feature and configure it. We need to provide a HTTP client instance, a provider lookup
where we determine the provider from the call (we don't need to put logic here since we are just supporting Google for this guide) and
a `urlProvider` giving the redirection URL that must match the one specified as authorized redirection in the Google Developers Console, in this case `http://me.mydomain.com:8080/login`:

```kotlin
install(Authentication) {
    oauth("google-oauth") {
        client = HttpClient(Apache)
        providerLookup = { googleOauthProvider }
        urlProvider = { redirectUrl("/login") }
    }
}

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}

```

Then we have to define the `/login` route that must be authenticated against our authentication provider.
When no get parameters are passed to that URL, the authentication feature will hook the handler, and will
redirect us to the OAuth Consent Screen from Google, and it will redirect us back to our `/login` route with the
`status` and `code` arguments that will be used by the authentication provider to call back to Google to obtain
an `accessToken` and attach a `OAuthAccessTokenResponse.OAuth2` principal to our call. And this time,
our handler will be executed.

We can retrieve that `accessToken` by getting the generated `OAuthAccessTokenResponse.OAuth2` principal and
`accessToken`. Then we can use the <https://www.googleapis.com/userinfo/v2/me>{target="_blank"} URL
with our `accessToken` passed as Authorization Bearer, to get a JSON with the user information.
You can check the contents of this JSON by using the [Google OAuth playground](https://developers.google.com/oauthplayground){target="_blank"}.

In this case, once we get the User ID, we are going to store it in a session, and then redirect to another place.

```kotlin
class MySession(val userId: String)

authenticate("google-oauth") {
    route("/login") {
        handle {
            val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                ?: error("No principal")

            val json = HttpClient(Apache).get<String>("https://www.googleapis.com/userinfo/v2/me") {
                header("Authorization", "Bearer ${principal.accessToken}")
            }

            val data = ObjectMapper().readValue<Map<String, Any?>>(json)
            val id = data["id"] as String?

            if (id != null) {
                call.sessions.set(MySession(id))
            }
            call.respondRedirect("/")
        }
    }
} 
```

>We have to install the Session feature first. Check the [Full Example](#full-example) for details.
>The ID from the user information is a string that looks like a number. Remember that JSON does not define long types,
>and that in cases like Twitter or Google, that have tons and tons of users and entities, that ID could be greater
>than 31 bits for a signed integer or even than 51 bits of precision from a standard Double.<br /> 
>As a rule of thumb you should always treat IDs and other number-like values as strings if you don't need
>to do arithmetic with them.
>
{type="note"}

## Full Example
{id="full-example"}

A simple embedded application would look like this:

<tabs>

```kotlin
val googleOauthProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "google",
    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
    accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
    requestMethod = HttpMethod.Post,

    clientId = "xxxxxxxxxxx.apps.googleusercontent.com", // @TODO: Remember to change this! 
    clientSecret = "yyyyyyyyyyy", // @TODO: Remember to change this! 
    defaultScopes = listOf("profile") // no email, but gives full name, picture, and id
)

class MySession(val userId: String)

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        install(Sessions) {
            cookie<MySession>("oauthSampleSessionId") {
                val secretSignKey = hex("000102030405060708090a0b0c0d0e0f") // @TODO: Remember to change this! 
                transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
            }
        }
        install(Authentication) {
            oauth("google-oauth") {
                client = HttpClient(Apache)
                providerLookup = { googleOauthProvider }
                urlProvider = {
                    redirectUrl("/login")
                }
            }
        }
        routing {
            get("/") {
                val session = call.sessions.get<MySession>()
                call.respondText("HI ${session?.userId}")
            }
            authenticate("google-oauth") {
                route("/login") {
                    handle {
                        val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                            ?: error("No principal")
            
                        val json = HttpClient(Apache).get<String>("https://www.googleapis.com/userinfo/v2/me") {
                            header("Authorization", "Bearer ${principal.accessToken}")
                        }
            
                        val data = ObjectMapper().readValue<Map<String, Any?>>(json)
                        val id = data["id"] as String?
            
                        if (id != null) {
                            call.sessions.set(MySession(id))
                        }
                        call.respondRedirect("/")
                    }
                }
            } 
        }
    }.start(wait = true)
}

private fun ApplicationCall.redirectUrl(path: String): String {
    val defaultPort = if (request.origin.scheme == "http") 80 else 443
    val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
    val protocol = request.origin.scheme
    return "$protocol://$hostPort$path"
}
```
</tabs>

## Testing

You can [provide a test HttpClient for testing OAuth](https://github.com/ktorio/ktor-samples/commit/56119d2879d9300cf51d66ea7114ff815f7db752).

## Additional resources

* Google OAuth playground: <https://developers.google.com/oauthplayground/>{target="_blank"}
* List of available google oauth scopes: <https://developers.google.com/identity/protocols/googlescopes>{target="_blank"} 
* Example with several oauth providers: <https://github.com/ktorio/ktor-samples/blob/1.3.0/feature/auth/src/io/ktor/samples/auth/OAuthLoginApplication.kt>{target="_blank"} 