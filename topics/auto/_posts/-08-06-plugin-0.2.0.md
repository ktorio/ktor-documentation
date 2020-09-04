[//]: # (layout: post)
[//]: # (title: Ktor IntelliJ IDEA Plugin 0.2.0)
[//]: # (categories: plugin)
[//]: # (featured: true)
[//]: # (#image: /blog/images/plugin.jpg)

Today we have released the 0.2.0 version of the plugin for IntelliJ IDEA.

### Swagger 2.0 (beta) and OpenAPI 3.0 (alpha)

This version allows to generate backend and frontend code from a Swagger/OpenAPI Model.

* Beta support for Swagger 2.0 JSON models.
* Alpha support for OpenAPI 3.0.0 JSON models (some code generated but misses some classes and other stuff).
* Generates a documented interface from the model.
* Generates a class implementing the interface mapped via reflection to specified routes.
* Creates a method for constructing a client from a URL endpoint and the model interface.
* Supports JSON schema validation for the model server-side.

You can try the [realworld swagger model](https://github.com/ktorio/ktor-init-tools/blob/5f72587a95da0eabf4ce106c2ca31cffdc22a155/ktor-generator/jvm/testresources/swagger.json).

### Fixes

* Fixes code generation on windows
* Fixes zip folder permissions in the website.
* Fixes maven missing property.
* Now generate spaces instead of tabs

### Improvements

* Generate `logback.xml` file.
* Additional templates.

You can read [more about the plugin here](intellj-idea.md).