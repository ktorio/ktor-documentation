[//]: # (title: Caching Headers)

The [CachingHeaders](https://api.ktor.io/%ktor_version%/io.ktor.features/-caching-headers/index.html) feature adds the capability to configure the `Cache-Control` and `Expires` headers used for HTTP caching. You can introduce different caching strategies for specific content types, such as images, CSS and JavaScript files, and so on.

## Install CachingHeaders {id="install_feature"}

<var name="feature_name" value="CachingHeaders"/>
<include src="lib.md" include-id="install_feature"/>

After installing `CachingHeaders`, you can [configure](#configure) caching settings for various content types.

## Configure Caching {id="configure"}
To configure the `CachingHeaders` feature, follow the steps below:
1. Define the [options](https://api.ktor.io/%ktor_version%/io.ktor.features/-caching-headers/-configuration/options.html) block that should provide caching options for a specified content type.
1. Specify the desired [ContentType](https://api.ktor.io/%ktor_version%/io.ktor.http/-content-type/index.html) as a parameter.
1. Create the [CachingOptions](https://api.ktor.io/%ktor_version%/io.ktor.http.content/-caching-options/index.html) object and pass the required `Cache-Control` and `Expires` headers as parameters:
    * The `cacheControl` parameter accepts a [CacheControl](https://api.ktor.io/%ktor_version%/io.ktor.http/-cache-control/index.html) value. You can use [CacheControl.MaxAge](https://api.ktor.io/%ktor_version%io.ktor.http/-cache-control/-max-age/index.html) to specify the `max-age` parameter and related settings, such as visibility, revalidation options, and so on. If necessary, you can disable caching by using `CacheControl.NoCache`/`CacheControl.NoStore`.
    * The `expires` parameter allows you to specify the `Expires` header as a `GMTDate` or `ZonedDateTime` value.
   
The code snippet below shows how to add the `Cache-Control` header with the `max-age` option for CSS content type:

```kotlin
install(CachingHeaders) {
    options { outgoingContent ->
        when (outgoingContent.contentType?.withoutParameters()) {
            ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
            else -> null
        }
    }
}
```


## Customize Headers for Specific Routes {id="route_headers"}

If you need to add caching headers for a specific route only, you can append desired headers into a response. The code snippet below shows how to do this for GET requests to `/profile`:

```kotlin
get("/profile") {
    call.response.headers.append(HttpHeaders.CacheControl, "no-cache, no-store")
    // ... 
}
```

You can learn more about routing in %product% from [](Routing_in_Ktor.md).
