[//]: # (title: Freemarker)
[//]: # (caption: Using Freemarker Templates)
[//]: # (category: servers)
[//]: # (keywords: html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.freemarker.FreeMarker)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/freemarker.html: - /features/freemarker.html)
[//]: # (- /features/templates/freemarker.html: - /features/templates/freemarker.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor includes support for [FreeMarker](http://freemarker.org/) templates through the FreeMarker
feature.  Initialize the FreeMarker feature with a
[TemplateLoader](http://freemarker.org/docs/pgui_config_templateloading.html):

```kotlin
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
```

This TemplateLoader sets up FreeMarker to look for the template files on the classpath in the
"templates" package, relative to the current class path.  A basic template looks like this:



```html
<html>
<h2>Hello ${user.name}!</h2>

Your email address is ${user.email}
</html>
```

With that template in `resources/templates` it is accessible elsewhere in the the application
using the `call.respond()` method:

```kotlin
data class User(val name: String, val email: String)

get("/") {
	val user = User("user name", "user@example.com")
	call.respond(FreeMarkerContent("hello.ftl", mapOf("user" to user)))
}
```