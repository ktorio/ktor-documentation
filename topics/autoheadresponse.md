[//]: # (title: Auto Head Response)


The `AutoHeadResponse` feature provides us with the ability to automatically respond to `HEAD` request for every route that has a `GET` defined. 

## Add Dependencies {id="add_dependencies"}
To enable `AutoHeadResponse` support, you need to include the `ktor-locations` artifact in the build script:
<var name="artifact_name" value="autoheadresponse"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Usage
In order to take advantage of this functionality, we need to install the `AutoHeadResponse` feature in our application


```kotlin
```
{src="/snippets/autohead/src/AutoHead.kt" include-symbol="main"}

In our case the `/home` route will now respond to `HEAD` request even though there is no explicit definition for this verb.

It's important to note that if we're using this feature, custom `HEAD` definitions for the same `GET` route will be ignored.


## Options
`AutoHeadResponse` does not provide any additional configuration options.
