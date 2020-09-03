[//]: # (title: Webjars)
[//]: # (caption: Webjars support)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/webjars.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.Webjars)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/webjars.html: - /features/webjars.html)
[//]: # (ktor_version_review: 1.0.0)

This feature enable serving static content provided by [webjars](https://www.webjars.org/). It allows you to package your assets such
as javascript libraries and css as part of your uber-jar.

## Installing the feature

{ #installing }

```kotlin
    install(Webjars) {
        path = "assets" //defaults to /webjars
        zone = ZoneId.of("EST") //defaults to ZoneId.systemDefault()
    }
```

This configures the feature to serve any webjars assets on the `/assets/` path. The `zone` argument configures the correct time zone to
be used with the `Last-Modified` header to support caching (only if [Conditional Headers](/servers/features/conditional-headers.html) feature is also installed).

{% include feature.html %}

## Versioning support

Webjars allow developers to change the versions of the dependencies without requiring a change on the path used to load them on your templates.

Let's assume you have imported `org.webjars:jquery:3.2.1`, you can use the following html code to import it:

```html
<head>
  <script src="/webjars/jquery/jquery.js"></script>
</head>  
```

You don't need to specify a version, should you choose to update your dependencies you don't need to modify your templates.