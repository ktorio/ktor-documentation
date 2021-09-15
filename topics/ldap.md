[//]: # (title: LDAP)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-server-auth</code>, <code>io.ktor:ktor-server-auth-ldap</code>
</p>
<var name="example_name" value="auth-ldap"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

LDAP is a protocol for working with various directory services that can store information about users. Ktor allows you to authenticate LDAP users using the [basic](basic.md), [digest](digest.md), or [form-based](form.md) authentications schemes.

> You can get general information about authentication and authorization in Ktor in the [](authentication.md) section.

## Add dependencies {id="add_dependencies"}
To enable `LDAP` authentication, you need to include the `ktor-server-auth` and `ktor-server-auth-ldap` artifacts in the build script:

<tabs group="languages">
    <tab title="Gradle (Groovy)" group-key="groovy">
        <code style="block" lang="Groovy" title="Sample">
            implementation "io.ktor:ktor-server-auth:$ktor_version"
            implementation "io.ktor:ktor-server-auth-ldap:$ktor_version"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)" group-key="kotlin">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("io.ktor:ktor-server-auth:$ktor_version")
            implementation("io.ktor:ktor-server-auth-ldap:$ktor_version")
        </code>
    </tab>
    <tab title="Maven" group-key="maven">
        <code style="block" lang="XML" title="Sample">
&lt;dependency&gt;
&lt;groupId&gt;io.ktor&lt;/groupId&gt;
&lt;artifactId&gt;ktor-server-auth&lt;/artifactId&gt;
&lt;version&gt;${ktor_version}&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
&lt;groupId&gt;io.ktor&lt;/groupId&gt;
&lt;artifactId&gt;ktor-server-auth-ldap&lt;/artifactId&gt;
&lt;version&gt;${ktor_version}&lt;/version&gt;
&lt;/dependency&gt;
        </code>
   </tab>
</tabs>


## Configure LDAP {id="configure"}

### Step 1: Choose an authentication provider {id="choose-auth"}

To authenticate LDAP users, you first need to choose an authentication provider for username and password validation. In Ktor, the [basic](basic.md), [digest](digest.md), or [form-based](form.md) providers can be used for this. For example, to use the `basic` authentication provider, call the [basic](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/basic.html) function inside the `install` block.

```kotlin
install(Authentication) {
    basic {
        validate { credentials ->
            // Authenticate an LDAP user
        }
    }
}
```

The [validate](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-basic-authentication-provider/-configuration/validate.html) function will be used to check user credentials.
 

### Step 2: Authenticate an LDAP user {id="authenticate"}

To authenticate an LDAP user, you need to call the [ldapAuthenticate](https://api.ktor.io/ktor-features/ktor-auth-ldap/ktor-auth-ldap/io.ktor.auth.ldap/ldap-authenticate.html) function. This function accepts [UserPasswordCredential](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-user-password-credential/index.html) and validates it against a specified LDAP server.

```kotlin
```
{src="snippets/auth-ldap/src/main/kotlin/com/example/Application.kt" lines="10-16"}

The `validate` function returns a [UserIdPrincipal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-user-id-principal/index.html) in a case of successful authentication or `null` if authentication fails.

Optionally, you can add additional validation for an authenticated user.

```kotlin
install(Authentication) {
    basic("auth-ldap") {
        validate { credentials ->
            ldapAuthenticate(credentials, "ldap://localhost:389", "cn=%s,dc=ktor,dc=io") {
                if (it.name == it.password) {
                    UserIdPrincipal(it.name)
                } else {
                    null
                }
            }
        }
    }
}
```


### Step 3: Define authorization scope {id="authenticate-route"}

After configuring LDAP, you can define the authorization for the different resources in our application using the `authenticate` function. In a case of successful authentication, you can retrieve an authenticated [UserIdPrincipal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/-user-id-principal/index.html) inside a route handler using the [call.principal](https://api.ktor.io/ktor-features/ktor-auth/ktor-auth/io.ktor.auth/principal.html) function and get a name of an authenticated user.

```kotlin
```
{src="snippets/auth-ldap/src/main/kotlin/com/example/Application.kt" lines="17-23"}

You can find the complete runnable example here: [auth-ldap](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/auth-ldap).

> Bear in mind that current LDAP implementation is synchronous.
