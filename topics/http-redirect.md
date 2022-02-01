[//]: # (title: Redirect)

By default, the Ktor client redirects to URLs provided in the `Location` header. If required, you can disable redirections.

## Add dependencies {id="add_dependencies"}
`HttpRedirect` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.

## Disable redirects {id="disable"}

To disable redirections, set the `followRedirects` property to `false` in a [client configuration block](create-client.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    followRedirects = false
}
```
