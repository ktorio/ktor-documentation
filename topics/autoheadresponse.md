[//]: # (title: Auto Head Response)

## Description
The `AutoHeadResponse` feature provides us with the ability to automatically respond to `HEAD` request for every route that has a `GET` defined. 

## Usage
In order to take advantage of this functionality, we need to install the `AutoHeadResponse` feature in our application


```kotlin
```
{src="/snippets/autohead/src/AutoHead.kt" include-symbol="main"}

In our case the `/home` route will now respond to `HEAD` request even though there is no explicit definition for this verb.

It's important to note that if we're using this feature, custom `HEAD` definitions for the same `GET` route will be ignored.

### Artifacts
The following artifacts need to be included in the build script to use this feature:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample">
        implementation "io.ktor.features.autoheadresponse:%\ktor_version%"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("io.ktor.features.autoheadresponse:%\ktor_version%")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample">
        <![CDATA[        
            <dependency>
                <scope>compile</scope>
                <groupId>io.ktor</groupId>
                <artifactId>autoheadresponse</artifactId>
                <version>%\ktor_version%</version>
            </dependency>
        ]]>
        </code>
   </tab>
</tabs>

## Options
`AutoHeadResponse` does not provide any additional configuration options.
