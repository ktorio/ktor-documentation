[//]: # (title: OAuth)

<include src="lib.md" include-id="outdated_warning"/>

<microformat>
<var name="example_name" value="auth-oauth-google"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

OAuth defines a mechanism for authentication using external providers like Google or Facebook safely.
You can read more about [OAuth](https://oauth.net/).
Ktor has a plugin (previously known as feature) to work with OAuth 1a and 2.0. 

## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="OAuth"/>
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.md" include-id="add_ktor_artifact_intro"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Usage {id="usage"}
A simplified OAuth 2.0 workflow:
* The client is redirected to an authorize URL for the specified provider (Google, Facebook, Twitter, Github...).
  specifying the `clientId` and a valid redirection URL.
* Once the login is correct, the provider generates an auth token using a `clientSecret` associated with that `clientId`.
* Then the client is redirected to a valid, previously agreed upon, application URL with an auth token that is signed with the `clientSecret`.
* Ktor's OAuth plugin verifies the token and generates a Principal `OAuthAccessTokenResponse`.
* With the auth token, you can request, for example, the user's email or id depending on the provider.


## Example {id="example"}

```kotlin
@Location("/login/{type?}") class login(val type: String = "")

val loginProviders = listOf(
    OAuthServerSettings.OAuth2ServerSettings(
            name = "github",
            authorizeUrl = "https://github.com/login/oauth/authorize",
            accessTokenUrl = "https://github.com/login/oauth/access_token",
            clientId = "***",
            clientSecret = "***"
    )
).associateBy {it.name}

install(Authentication) {
    oauth("gitHubOAuth") {
        client = HttpClient(Apache)
        providerLookup = { loginProviders[application.locations.resolve<login>(login::class, this).type] }
        urlProvider = { url(login(it.name)) }
    }
}

routing {
    authenticate("gitHubOAuth") {
        location<login>() {
            param("error") {
                handle {
                    call.loginFailedPage(call.parameters.getAll("error").orEmpty())
                }
            }
        
            handle {
                val principal = call.authentication.principal<OAuthAccessTokenResponse>()
                if (principal != null) {
                    call.loggedInSuccessResponse(principal)
                } else {
                    call.loginPage()
                }
            }
        }
    }
}
```

Depending on the OAuth version, you will get a different Principal

```kotlin
sealed class OAuthAccessTokenResponse : Principal {
    data class OAuth1a(
        val token: String, val tokenSecret: String,
        val extraParameters: Parameters = Parameters.Empty
    ) : OAuthAccessTokenResponse()

    data class OAuth2(
        val accessToken: String, val tokenType: String,
        val expiresIn: Long, val refreshToken: String?,
        val extraParameters: Parameters = Parameters.Empty
    ) : OAuthAccessTokenResponse()
}
```

## Guide, example and testing

* [Example configuring several OAuth providers](https://github.com/ktorio/ktor-samples/blob/1.3.0/feature/auth/src/io/ktor/samples/auth/OAuthLoginApplication.kt)
* [Testing OAuth authentication](https://github.com/ktorio/ktor-samples/commit/56119d2879d9300cf51d66ea7114ff815f7db752)