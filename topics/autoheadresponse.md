[//]: # (title: AutoHeadResponse)

<microformat>
<var name="example_name" value="autohead"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>


The `AutoHeadResponse` plugin provides us with the ability to automatically respond to `HEAD` request for every route that has a `GET` defined.

## Add dependencies {id="add_dependencies"}
To use `AutoHeadResponse`, you need to include the `ktor-server-auto-head-response` artifact in the build script:
<var name="artifact_name" value="ktor-server-auto-head-response"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Usage
In order to take advantage of this functionality, we need to install the `AutoHeadResponse` plugin in our application.


```kotlin
```
{src="/snippets/autohead/src/AutoHead.kt" include-symbol="main"}

In our case the `/home` route will now respond to `HEAD` request even though there is no explicit definition for this verb.

It's important to note that if we're using this plugin, custom `HEAD` definitions for the same `GET` route will be ignored.


## Options
`AutoHeadResponse` does not provide any additional configuration options.
