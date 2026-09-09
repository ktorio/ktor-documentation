package com.example.oauth.google

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.util.collections.ConcurrentMap
import kotlinx.html.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

val applicationHttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

fun Application.main(
    httpClient: HttpClient = applicationHttpClient
) {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    val googleAuthorizeUrl =
        "https://accounts.google.com/o/oauth2/auth"
    val googleTokenUrl =
        "https://accounts.google.com/o/oauth2/token"
    val profileScope =
        "https://www.googleapis.com/auth/userinfo.profile"
    val googleClientId =
        System.getenv("GOOGLE_CLIENT_ID").orEmpty()
    val googleClientSecret =
        System.getenv("GOOGLE_CLIENT_SECRET").orEmpty()
    val redirects = ConcurrentMap<String, String>()
    install(Authentication) {
        oauth("auth-oauth-google") {
            // Configure oauth authentication
            urlProvider = { "http://localhost:8080/callback" }
            settings = OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = googleAuthorizeUrl,
                    accessTokenUrl = googleTokenUrl,
                    requestMethod = HttpMethod.Post,
                    clientId = googleClientId,
                    clientSecret = googleClientSecret,
                    defaultScopes = listOf(profileScope),
                    extraAuthParameters =
                        listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        // saves new state with redirect url
                        val query = call.request.queryParameters
                        query["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    }
                )
            fallback = { cause ->
                if (cause is OAuth2RedirectError) {
                    respondRedirect("/login-after-fallback")
                } else {
                    respond(
                        HttpStatusCode.Forbidden,
                        cause.message
                    )
                }
            }
            client = httpClient
        }
    }
    routing {
        authenticate("auth-oauth-google") {
            get("/login") {
                // Redirects to 'authorizeUrl' automatically
            }

            get("/callback") {
                val currentPrincipal:
                    OAuthAccessTokenResponse.OAuth2? =
                        call.principal()
                // redirects home if the url is not found
                // before authorization
                currentPrincipal?.let { principal ->
                    principal.state?.let { state ->
                        val session = UserSession(
                            state,
                            principal.accessToken
                        )
                        call.sessions.set(session)
                        redirects.remove(state)?.let { url ->
                            call.respondRedirect(url)
                            return@get
                        }
                    }
                }
                call.respondRedirect("/home")
            }
        }
        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/login") { +"Login with Google" }
                    }
                }
            }
        }
        get("/home") {
            val userSession: UserSession? = getSession(call)
            if (userSession != null) {
                val userInfo: UserInfo =
                    getPersonalGreeting(httpClient, userSession)
                val name = userInfo.name
                call.respondText("Hello, $name! Welcome home!")
            }
        }
        get("/{path}") {
            val userSession: UserSession? = getSession(call)
            if (userSession != null) {
                val userInfo: UserInfo =
                    getPersonalGreeting(httpClient, userSession)
                call.respondText("Hello, ${userInfo.name}!")
            }
        }
        get("/login-after-fallback") {
            call.respondText("Redirected after fallback")
        }
    }
}

private suspend fun getPersonalGreeting(
    httpClient: HttpClient,
    userSession: UserSession
): UserInfo = httpClient.get(
    "https://www.googleapis.com/oauth2/v2/userinfo"
) {
    headers {
        val bearer = "Bearer ${userSession.token}"
        append(HttpHeaders.Authorization, bearer)
    }
}.body()

private suspend fun getSession(
    call: ApplicationCall
): UserSession? {
    val userSession: UserSession? = call.sessions.get()
    //if there is no session, redirect to login
    if (userSession == null) {
        val loginUrl = "http://localhost:8080/login"
        val redirectUrl = URLBuilder(loginUrl).run {
            parameters.append("redirectUrl", call.request.uri)
            build()
        }
        call.respondRedirect(redirectUrl)
        return null
    }
    return userSession
}

@Serializable
data class UserSession(val state: String, val token: String)

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    @SerialName("given_name") val givenName: String,
    val picture: String,
)
