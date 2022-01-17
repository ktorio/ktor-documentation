[//]: # (title: Authentication and authorization)

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-auth</code>
</p>
</microformat>

<excerpt>
The Auth plugin handles authentication and authorization in your client application.
</excerpt>

Ktor provides the `Auth` plugin to handle authentication and authorization in your client application. Typical usage scenarios include logging in users and gaining access to specific resources. 


## Supported authentication types {id="supported"}

HTTP provides a [general framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) for access control and authentication. The Ktor client allows you to use the following HTTP authentication schemes:

* [Basic](basic-client.md) - uses `Base64` encoding to provide a username and password. Generally is not recommended if not used in combination with HTTPS.
* [Digest](digest-client.md) - an authentication method that communicates user credentials in an encrypted form by applying a hash function to the username and password.
* [Bearer](bearer-client.md) - an authentication scheme that involves security tokens called bearer tokens. For example, you can use this scheme as a part of OAuth flow to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and so on.

## Add dependencies {id="add_dependencies"}

To enable authentication, you need to include the `ktor-client-auth` artifact in the build script:

<var name="artifact_name" value="ktor-client-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install Auth {id="install_plugin"}
To install the `Auth` plugin, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    install(Auth) {
        // Configure authentication
    }
}
```
Now you can [configure](#configure_authentication) the required authentication provider.



## Configure authentication {id="configure_authentication"}

To learn how to configure the desired [authentication provider](#supported), see a corresponding topic:
* [](basic-client.md)
* [](digest-client.md)
* [](bearer-client.md)