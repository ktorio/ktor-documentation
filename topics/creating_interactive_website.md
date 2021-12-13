[//]: # (title: Adding interactivity using templates)

<microformat>
<var name="example_name" value="tutorial-website-interactive"/>
<include src="lib.xml" include-id="download_example"/>
<p>
Used plugins: <a href="Routing_in_Ktor.md">Routing</a>, <a href="freemarker.md">FreeMarker</a>
</p>
</microformat>


<excerpt>Learn how to create an interactive website using HTML templating engines like FreeMarker.</excerpt>

In this series of tutorials, we'll show you how to create a website in Ktor:
- In the first tutorial, we showed how to host [static content](creating_static_website.md) like images and HTML pages.
- In this tutorial, we'll make our website interactive and create a simple blog application using the [FreeMarker](https://freemarker.apache.org/) template engine.
- Finally, we'll [add persistence](interactive_website_add_persistence.md) to our website using the Exposed framework.

Before starting this tutorial, create a static website as described in [](creating_static_website.md).

> You can the result of the previous tutorial here: [tutorial-website-static](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-website-static).


## Adjust FreeMarker configuration {id="freemarker_config"}

The Ktor plugin for IntelliJ IDEA already [generated code](creating_static_website.md#source_code) for the FreeMarker plugin in the `plugins/Templating.kt` file:

```kotlin
```
{src="snippets/tutorial-website-static/src/main/kotlin/com/example/plugins/Templating.kt" lines="3-11"}

The `templateLoader` setting tells our application that FreeMarker templates will be located in the `templates` directory. Let's also add the `outputFormat` as follows:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Templating.kt" lines="3-13"}

The `outputFormat` setting helps convert control characters provided by the user to their corresponding HTML entities. This ensures that when one of our journal entries contains a String like `<b>Hello</b>`, it is actually printed as `<b>Hello</b>`, not **Hello**. This so-called [escaping](https://freemarker.apache.org/docs/dgui_misc_autoescaping.html) is an essential step in preventing [XSS attacks](https://owasp.org/www-community/attacks/xss/).

## Create a model {id="model"}

Create a `models` package inside `com.example` and add `Article.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/models/Article.kt"}

## Show a list of articles {id="list_articles"}

### Serve the templated content {id="serve_template"}

Open `Routing.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="3-24,61-63"}

### Create a template {id="create_template"}

Create `index.ftl` inside `resources/templates/`:

```html
    <#-- @ftlvariable name="articles" type="kotlin.collections.List<com.example.models.Article>" -->
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <title>Kotlin Journal</title>
    </head>
    <body style="text-align: center; font-family: sans-serif">
    <img src="/static/ktor_logo.png">
    <h1>Kotlin Ktor Journal </h1>
    <p><i>Powered by Ktor & Freemarker!</i></p>
    <hr>
    <#list articles?reverse as article>
    <div>
        <h3>
            <a href="/articles/${article.id}">${article.title}</a>
        </h3>
        <p>
            ${article.body}
        </p>
    </div>
    </#list>
    <hr>
    <p>
        <a href="/articles/new">Create article</a>
    </p>
    </body>
    </html>
```

Mention [@ftlvariable](https://www.jetbrains.com/help/idea/template-data-languages.html#special-comments).

### Refactor a template {id="refactor_template"}

Create `resources/templates/_layout.ftl` using [macros](https://freemarker.apache.org/docs/ref_directive_macro.html):

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/_layout.ftl"}

Update `index.ftl`:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/index.ftl"}




## Create a new article {id="new_article"}

Open `Routing.kt` and add inside `route("articles")`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="25-27"}


New template `new.ftl`:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/new.ftl"}

Back to `Routing.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="32-39"}


## Show a created article {id="show_article"}

`Routing.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="28-31"}

New template `show.ftl`: 

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/show.ftl"}


## Edit or delete an article {id="edit_article"}

`Routing.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="40-43"}

New template `edit.ftl`:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/edit.ftl"}

Back to `Routing.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="44-60"}



## We're done! {id="done"}

It's time to pat ourselves on the back – we've put together a nice little journal application, and have learned about many topics in the meantime. From static files to templating, from basic routing to kotlinx.html, we've covered a lot of ground.

![](ktor_journal.png){animated="true" width="706"}

This concludes the guided part of this tutorial. We have included the final state of the journal application in the [codeSnippets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets) project: [tutorial-website-interactive](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-website-interactive). But of course, your journey doesn't have to stop here. Check out the [What's next](#whats_next) section to get an idea of how you could expand the application, and where to go if you need help in your endeavors!


## What's next {id="whats_next"}

At this point, you should have gotten a basic idea of how to serve static files with Ktor and how the integration of plugins such as FreeMarker or kotlinx.html can enable you to write basic applications that can even react to user input.

### Feature requests {id="feature_requests"}

At this point, our journal application is still rather barebones, so of course, it might be a fun challenge to add more features to the project and learn even more about building interactive sites with Kotlin and Ktor. To get you started, here are a few ideas of how the application could still be improved, in no particular order:

- **Authentication!** Our current version of the journal allows all visitors to post content to our journal. We could use [Ktor's authentication plugins](authentication.md) to ensure that only select users can post to the journal while keeping read access open to everyone.
- **Make it look nicer!** The stylesheets for the journal are currently rudimentary at best. Consider creating your own style sheet and serving it as a static `.css` file from Ktor!
- **Organize your routes!** As the complexity of our application increases, so does the number of routes we try to support. For bigger applications, we usually want to add structure to our routing – like separating routes out into separate files. If you'd like to learn about different ways to organize your routes with Ktor, check out the [](Routing_in_Ktor.md) help topic.
