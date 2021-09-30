package io.ktor.snippets.auth

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.html.*
import java.util.concurrent.*

@Location("/") class index
@Location("/login/{type?}") class login(val type: String = "")

/**
 * DISCLAIMER
 *
 * The constants are only for demo purposes. You should NEVER keep secret keys in the source code
 * but store them safe externally instead.
 * You also can keep them encrypted but in this case you have to keep encryption key safe
 *
 * Also you SHOULD ALWAYS use HTTPS with OAuth2
 */
val loginProviders = listOf(
        OAuthServerSettings.OAuth1aServerSettings(
                name = "twitter",
                requestTokenUrl = "https://api.twitter.com/oauth/request_token",
                authorizeUrl = "https://api.twitter.com/oauth/authorize",
                accessTokenUrl = "https://api.twitter.com/oauth/access_token",
                consumerKey = "***",
                consumerSecret = "***",
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "vk",
                authorizeUrl = "https://oauth.vk.com/authorize",
                accessTokenUrl = "https://oauth.vk.com/access_token",
                clientId = "***",
                clientSecret = "***",
                authorizeUrlInterceptor = { parameters.urlEncodingOption = UrlEncodingOption.DEFAULT }
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "github",
                authorizeUrl = "https://github.com/login/oauth/authorize",
                accessTokenUrl = "https://github.com/login/oauth/access_token",
                clientId = "***",
                clientSecret = "***",
                authorizeUrlInterceptor = { parameters.urlEncodingOption = UrlEncodingOption.DEFAULT }
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
                requestMethod = HttpMethod.Post,
                clientId = "***.apps.googleusercontent.com",
                clientSecret = "***",
                defaultScopes = listOf("https://www.googleapis.com/auth/plus.login"),
                authorizeUrlInterceptor = { parameters.urlEncodingOption = UrlEncodingOption.DEFAULT }
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "facebook",
                authorizeUrl = "https://graph.facebook.com/oauth/authorize",
                accessTokenUrl = "https://graph.facebook.com/oauth/access_token",
                requestMethod = HttpMethod.Post,
                clientId = "***",
                clientSecret = "***",
                defaultScopes = listOf("public_profile"),
                authorizeUrlInterceptor = { parameters.urlEncodingOption = UrlEncodingOption.DEFAULT }
        ),
        OAuthServerSettings.OAuth2ServerSettings(
                name = "hub",
                authorizeUrl = "http://localhost:9099/api/rest/oauth2/auth",
                accessTokenUrl = "http://localhost:9099/api/rest/oauth2/token",
                requestMethod = HttpMethod.Post,
                clientId = "***",
                clientSecret = "***",
                defaultScopes = listOf("***"),
                accessTokenRequiresBasicAuth = true,
                authorizeUrlInterceptor = { parameters.urlEncodingOption = UrlEncodingOption.DEFAULT }
        )
).associateBy { it.name }

fun Application.OAuthLoginApplication() {
    OAuthLoginApplicationWithDeps(
        oauthHttpClient = HttpClient(Apache).apply {
            environment.monitor.subscribe(ApplicationStopping) {
                close()
            }
        }
    )
}

fun Application.OAuthLoginApplicationWithDeps(oauthHttpClient: HttpClient) {
    val authOauthForLogin = "authOauthForLogin"

    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(Authentication) {
        oauth(authOauthForLogin) {
            client = oauthHttpClient
            providerLookup = {
                loginProviders[application.locations.resolve<login>(login::class, this).type]
            }
            urlProvider = { p -> redirectUrl(login(p.name), false) }
        }
    }


    install(Routing) {
        get<index> {
            call.respondHtml {
                head {
                    title { +"index page" }
                }
                body {
                    h1 {
                        +"Try to login"
                    }
                    p {
                        a(href = locations.href(login())) {
                            +"Login"
                        }
                    }
                }
            }
        }

        authenticate(authOauthForLogin) {
            location<login> {
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
}

private fun <T : Any> ApplicationCall.redirectUrl(t: T, secure: Boolean = true): String {
    val hostPort = request.host() + request.port().let { port -> if (port == 80) "" else ":$port" }
    val protocol = when {
        secure -> "https"
        else -> "http"
    }
    return "$protocol://$hostPort${application.locations.href(t)}"
}

private suspend fun ApplicationCall.loginPage() {
    respondHtml {
        head {
            title { +"Login with" }
        }
        body {
            h1 {
                +"Login with:"
            }

            for (p in loginProviders) {
                p {
                    a(href = application.locations.href(login(p.key))) {
                        +p.key
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.loginFailedPage(errors: List<String>) {
    respondHtml {
        head {
            title { +"Login with" }
        }
        body {
            h1 {
                +"Login error"
            }

            for (e in errors) {
                p {
                    +e
                }
            }
        }
    }
}

private suspend fun ApplicationCall.loggedInSuccessResponse(callback: OAuthAccessTokenResponse) {
    respondHtml {
        head {
            title { +"Logged in" }
        }
        body {
            h1 {
                +"You are logged in"
            }
            p {
                +"Your token is $callback"
            }
        }
    }
}
