[//]: # (title: Thymeleaf)

Ktor allows you to use [Thymeleaf templates](https://www.thymeleaf.org/) as views within your application by installing the [Thymeleaf](https://api.ktor.io/%ktor_version%/io.ktor.thymeleaf/-thymeleaf/index.html) feature.


## Add Dependencies {id="add_dependencies"}
<var name="feature_name" value="Thymeleaf"/>
<var name="artifact_name" value="ktor-thymeleaf"/>
<include src="lib.md" include-id="add_ktor_artifact_intro"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

## Install Thymeleaf {id="install_feature"}

<var name="feature_name" value="Thymeleaf"/>
<include src="lib.md" include-id="install_feature"/>



## Configure Thymeleaf {id="configure"}
### Configure Template Loading {id="template_loading"}
Inside the `install` block, you can configure the `ClassLoaderTemplateResolver`. For example, the code snippet below enables Ktor to look up `*.html` templates in the `templates` package relative to the current classpath:
```kotlin
import io.ktor.thymeleaf.*

install(Thymeleaf) {
    setTemplateResolver(ClassLoaderTemplateResolver().apply {
        prefix = "templates/"
        suffix = ".html"
        characterEncoding = "utf-8"
    })
}
```

### Send a Template in Response {id="use_template"}
Imagine you have the `index.html` template in `resources/templates`:
```html
<html xmlns:th="http://www.thymeleaf.org">
    <body>
        <h1 th:text="'Hello, ' + ${user.name}"></h1>
    </body>
</html>
```

A data model for a user looks as follows:
```kotlin
data class User(val id: Int, val name: String)
```

To use the template for the specified [route](Routing_in_Ktor.md), pass `ThymeleafContent` to the `call.respond` method in the following way:
```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(ThymeleafContent("index", mapOf("user" to sampleUser)))
}
```
