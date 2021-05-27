[//]: # (title: AutoHeadResponse)

<microformat>
<var name="example_name" value="autohead"/>
<include src="lib.md" include-id="download_example"/>
</microformat>


The `AutoHeadResponse` plugin (previously known as feature) provides us with the ability to automatically respond to `HEAD` request for every route that has a `GET` defined.

## Usage
In order to take advantage of this functionality, we need to install the `AutoHeadResponse` plugin in our application


```kotlin
```
{src="/snippets/autohead/src/AutoHead.kt" include-symbol="main"}

In our case the `/home` route will now respond to `HEAD` request even though there is no explicit definition for this verb.

It's important to note that if we're using this plugin, custom `HEAD` definitions for the same `GET` route will be ignored.


## Options
`AutoHeadResponse` does not provide any additional configuration options.
