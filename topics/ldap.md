[//]: # (title: LDAP)

<include src="lib.md" include-id="outdated_warning"/>

## Add Dependencies {id="add_dependencies"}
To enable `LDAP` authentication, you need to include the `ktor-auth` and `ktor-auth-ldap` artifacts in the build script:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample">
            implementation "io.ktor:ktor-auth:$ktor_version"
            implementation "io.ktor:ktor-auth-ldap:$ktor_version"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("io.ktor:ktor-auth:$ktor_version")
            implementation("io.ktor:ktor-auth-ldap:$ktor_version")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample">
        <![CDATA[
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-auth</artifactId>
            <version>${ktor_version}</version>
        </dependency>
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-auth-ldap</artifactId>
            <version>${ktor_version}</version>
        </dependency>
        ]]>
        </code>
   </tab>
</tabs>


## Usage {id="usage"}
Ktor supports LDAP (Lightweight Directory Access Protocol) for credential authentication.

```kotlin
authentication {
    basic("authName") {
        realm = "realm"
        validate { credential ->
            ldapAuthenticate(credential, "ldap://$localhost:${ldapServer.port}", "uid=%s,ou=system")
        }
    }
}
```

Optionally you can define an additional validation check:
```kotlin
authentication {
    basic("authName") { 
        realm = "realm"
        validate { credential ->
            ldapAuthenticate(credentials, "ldap://localhost:389", "cn=%s ou=users") {
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

This signature looks like this:

```kotlin
// Simplified signatures
fun ldapAuthenticate(credential: UserPasswordCredential, ldapServerURL: String, userDNFormat: String): UserIdPrincipal?
fun ldapAuthenticate(credential: UserPasswordCredential, ldapServerURL: String, userDNFormat: String, validate: InitialDirContext.(UserPasswordCredential) -> UserIdPrincipal?): UserIdPrincipal?
```

To support more complex scenarios, there is a more complete signature for `ldapAuthenticate`:

```kotlin
fun <K : Credential, P : Any> ldapAuthenticate(credential: K, ldapServerURL: String, ldapEnvironmentBuilder: (MutableMap<String, Any?>) -> Unit = {}, doVerify: InitialDirContext.(K) -> P?): P?
```

While the other overloads support only `UserPasswordCredential`, this overload accept any kind of credential. And instead of receiving a string with the userDNFormat, you can provide a generator
to populate a map with the environments for ldap.

A more advanced example using this:

```kotlin
application.install(Authentication) {
    basic {
        validate { credential ->
            ldapAuthenticate(
                credential,
                "ldap://$localhost:${ldapServer.port}",
                configure = { env: MutableMap<String, Any?> -> 
                    env.put("java.naming.security.principal", "uid=admin,ou=system")
                    env.put("java.naming.security.credentials", "secret")
                    env.put("java.naming.security.authentication", "simple")
                }
            ) {
                val users = (lookup("ou=system") as LdapContext).lookup("ou=users") as LdapContext
                val controls = SearchControls().apply {
                    searchScope = SearchControls.ONELEVEL_SCOPE
                    returningAttributes = arrayOf("+", "*")
                }

                users.search("", "(uid=user-test)", controls).asSequence().firstOrNull {
                    val ldapPassword = (it.attributes.get("userPassword")?.get() as ByteArray?)?.toString(Charsets.ISO_8859_1)
                    ldapPassword == credential.password
                }?.let { UserIdPrincipal(credential.name) }
            }
        }
    }
}
```

You can see [advanced examples for LDAP authentication](https://github.com/ktorio/ktor/blob/main/ktor-features/ktor-auth-ldap/test/io/ktor/tests/auth/ldap/LdapAuthTest.kt) in the Ktor's tests.



Bear in mind that current LDAP implementation is synchronous.
{ .performance.note}