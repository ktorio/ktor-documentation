[//]: # (title: Session authentication)

<include src="lib.md" include-id="outdated_warning"/>

<microformat>
<var name="example_name" value="auth-session"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

[Sessions](sessions.md) provide a mechanism to persist data between different HTTP requests. Typical use cases include storing a logged-in user's ID, the contents of a shopping basket, or keeping user preferences on the client. In Ktor, a user that already has an associated session can be authenticated using the `session` provider. 

## Example

```kotlin
```
{src="snippets/auth-session/src/main/kotlin/com/example/Application.kt"}




