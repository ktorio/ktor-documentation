[//]: # (title: Form)

<include src="lib.md" include-id="outdated_warning"/>

<microformat>
<var name="example_name" value="auth-form"/>
<include src="lib.md" include-id="download_example"/>
</microformat>


## Add dependencies {id="add_dependencies"}
To enable `form` authentication, you need to include the `ktor-auth` artifact in the build script:
<var name="artifact_name" value="ktor-auth"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


## Usage {id="usage"} 

The `form` authentication uses a username and password as credentials:

```kotlin
```
{src="snippets/auth-form/src/main/kotlin/com/example/Application.kt" lines="9-20"}

The `validate` method provides a callback that must generate a Principal from given a `UserPasswordCredential`
or null for invalid credentials. That callback is marked as *suspending*, so that you can validate credentials in an asynchronous fashion.