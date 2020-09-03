[//]: # (title: Auto Head Response)
[//]: # (caption: Enable Automatic HEAD Responses)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/autoheadresponse.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.AutoHeadResponse)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/autoheadresponse.html: - /features/autoheadresponse.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor can automatically provide responses to `HEAD` requests for existing routes that have the `GET` verb defined. 

{% include feature.html %}

## Usage

To enable automatic `HEAD` responses, install the `AutoHeadResponse` feature

```kotlin
fun Application.main() {
  // ...
  install(AutoHeadResponse) 
  // ...
}
```

## Configuration options

None.

## Under the covers

This feature automatically responds to `HEAD` requests by routing as if it were `GET` response and discarding 
the body. Since any `FinalContent` produced by the system has lazy content semantics, it does not incur in any performance
costs for processing a `GET` request with a body. 