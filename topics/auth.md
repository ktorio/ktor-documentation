[//]: # (title: Authentication and authorization)

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-auth</code>
</p>
</tldr>

<link-summary>
The Auth plugin handles authentication and authorization in your client application.
</link-summary>

Ktor provides the [Auth](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-auth/io.ktor.client.plugins.auth/-auth/index.html) plugin to handle authentication and authorization in your client application. 
Typical usage scenarios include logging in users and gaining access to specific resources. 


## Supported authentication types {id="supported"}

HTTP provides a [general framework](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication) for access control and authentication. The Ktor client allows you to use the following HTTP authentication schemes:

* [Basic](basic-client.md) - uses `Base64` encoding to provide a username and password. Generally is not recommended if not used in combination with HTTPS.
* [Digest](digest-client.md) - an authentication method that communicates user credentials in an encrypted form by applying a hash function to the username and password.
* [Bearer](bearer-client.md) - an authentication scheme that involves security tokens called bearer tokens. For example, you can use this scheme as a part of OAuth flow to authorize users of your application by using external providers, such as Google, Facebook, Twitter, and so on.

## Add dependencies {id="add_dependencies"}

To enable authentication, you need to include the `ktor-client-auth` artifact in the build script:

<var name="artifact_name" value="ktor-client-auth"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>
<include src="lib.xml" include-id="add_ktor_client_artifact_tip"/>


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

### Step 1: Choose an authentication provider {id="choose-provider"}

To use a specific authentication provider ([basic](basic-client.md), [digest](digest-client.md), or [bearer](bearer-client.md)), you need to call the corresponding function inside the `install` block. For example, to use the `basic` authentication, call the [basic](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-auth/io.ktor.client.plugins.auth.providers/basic.html) function:

```kotlin
install(Auth) {
    basic {
        // Configure basic authentication
    }
}
```
Inside the block, you can configure settings specific to this provider.


### Step 2: (Optional) Configure the realm {id="realm"}

Optionally, you can configure the realm using the `realm` property:

```kotlin
install(Auth) {
    basic {
        realm = "Access to the '/' path"
        // ...
    }
}
```

You can create several providers with different realms to access different resources:

```kotlin
install(Auth) {
    basic {
        realm = "Access to the '/' path"
        // ...
    }
    basic {
        realm = "Access to the '/admin' path"
        // ...
    }
}
```

In this case, the client chooses the necessary provider based on the `WWW-Authenticate` response header, which contains the realm.


### Step 3: Configure a provider {id="configure-provider"}

To learn how to configure settings for a specific [provider](#supported), see a corresponding topic:
* [](basic-client.md)
* [](digest-client.md)
* [](bearer-client.md)
