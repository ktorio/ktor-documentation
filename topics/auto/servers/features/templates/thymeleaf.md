[//]: # (title: Thymeleaf)
[//]: # (caption: Using Thymeleaf Templates)
[//]: # (category: servers)
[//]: # (keywords: html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.thymeleaf.Thymeleaf)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/thymeleaf.html: - /features/thymeleaf.html)
[//]: # (- /features/templates/thymeleaf.html: - /features/templates/thymeleaf.html)
[//]: # (ktor_version_review: 1.2.0)

Ktor includes support for [Thymeleaf](https://www.thymeleaf.org/) templates through the Thymeleaf
feature.  Initialize the Thymeleaf feature with a
[ClassLoaderTemplateResolver](https://www.thymeleaf.org/apidocs/thymeleaf/3.0.1.RELEASE/org/thymeleaf/templateresolver/ClassLoaderTemplateResolver.html):

```kotlin
install(Thymeleaf) {
    setTemplateResolver(ClassLoaderTemplateResolver().apply { 
        prefix = "templates/"
        suffix = ".html"
        characterEncoding = "utf-8"
    })
}
```

This TemplateResolver sets up Thymeleaf to look for the template files on the classpath in the
"templates" package, relative to the current class path.  A basic template looks like this:



```html
<!DOCTYPE html >
<html xmlns:th="http://www.thymeleaf.org">
<body>
<h2 th:text="'Hello ' + ${user.name} + '!'"></h2>
<p>Your email address is <span th:text="${user.email}"></span></p>
</body>
</html>
```

With that template in `resources/templates` it is accessible elsewhere in the the application
using the `call.respond()` method:

```kotlin
data class User(val name: String, val email: String)

get("/") {
    val user = User("user name", "user@example.com")
    call.respond(ThymeleafContent("hello", mapOf("user" to user)))
    }
```