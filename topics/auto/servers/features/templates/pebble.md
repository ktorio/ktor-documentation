[//]: # (title: Pebble)
[//]: # (caption: Using Pebble Templates)
[//]: # (category: servers)
[//]: # (keywords: html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.pebble.Pebble)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/pebble.html: - /features/pebble.html)
[//]: # (- /features/templates/pebble.html: - /features/templates/pebble.html)
[//]: # (ktor_version_review: 1.3.0)

Ktor includes support for [Pebble](https://pebbletemplates.io) templates through the PEbble
feature.  Initialize the Pebble feature with the
[PebbleEngine.Builder](https://pebbletemplates.io/com/mitchellbosecke/pebble/PebbleEngine/Builder/):

{% include feature.html %}

## Installation
{id="installation"}

You can install Pebble, and configure the `PebbleEngine.Builder`.

```kotlin
install(Pebble) { // this: PebbleEngine.Builder
    loader(ClasspathLoader().apply {
        prefix= 
    })
}
```

This loader will look for the template files on the classpath in the "templates" package.

## Usage
{id="usage"}

A basic template looks like this:

{% raw %}
```html
<html>
<h2>Hello, {{ user.name }}!</h2>

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
	 call.respond(PebbleContent("hello.html", mapOf("user" to user)))
}
```