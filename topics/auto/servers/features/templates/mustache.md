[//]: # (title: Mustache)
[//]: # (caption: Using Mustache Templates)
[//]: # (category: servers)
[//]: # (keywords: html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.mustache.Mustache)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/mustache.html: - /features/mustache.html)
[//]: # (- /features/templates/mustache.html: - /features/templates/mustache.html)
[//]: # (ktor_version_review: 1.1.1)

Ktor includes support for [Mustache](https://github.com/spullara/mustache.java) templates through the Mustache
feature.  Initialize the Mustache feature with a
[MustacheFactory](http://spullara.github.io/mustache/apidocs/com/github/mustachejava/MustacheFactory.html):

```kotlin
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }
```

This MustacheFactory sets up Mustache to look for the template files on the classpath in the
"templates" package, relative to the current class path.  A basic template looks like this:



{% raw %}
```html
<html>
<h1>Hello {{ user.name }}</h1>

Your email address is {{ user.email }}
</html>
```
{% endraw %}

With that template in `resources/templates` it is accessible elsewhere in the the application
using the `call.respond()` method:

```kotlin
data class User(val name: String, val email: String)

get("/") {
    val user = User("user name", "user@example.com")
    call.respond(MustacheContent("hello.hbs", mapOf("user" to user)))
}
```