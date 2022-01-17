[//]: # (title: Creating an interactive website)

<microformat>
<var name="example_name" value="tutorial-website-interactive"/>
<include src="lib.xml" include-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="Routing_in_Ktor.md">Routing</a>, <a href="freemarker.md">FreeMarker</a>
</p>
</microformat>


<excerpt>Learn how to create an interactive website using HTML templating engines like FreeMarker.</excerpt>

In this series of tutorials, we'll show you how to create a simple blog application in Ktor:
- In the first tutorial, we showed how to host [static content](creating_static_website.md) like images and HTML pages.
- In this tutorial, we'll make our application interactive using the [FreeMarker](https://freemarker.apache.org/) template engine.
- Finally, we'll [add persistence](interactive_website_add_persistence.md) to our website using the Exposed framework.


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

First, we need to create a model describing an article in our journal application. Create a `models` package inside `com.example`, add the `Article.kt` file inside the created package, and insert the following code:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/models/Article.kt" lines="1-12"}

An article has three attributes: `id`, `title`, and `body`. The `title` and `body` attributes can be specified directly while a unique `id` is generated automatically using `AtomicInteger` - a thread-safe data structure that ensures that two articles will never receive the same ID.


Inside `Article.kt`, let's create a mutable list for storing articles and add the first entry:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/models/Article.kt" lines="14-17"}


## Define routes {id="routes"}

Now we are ready to define [routes](Routing_in_Ktor.md) for our journal. Open the `com/example/plugins/Routing.kt` file and add the following code inside `configureRouting`:

```kotlin
fun Application.configureRouting() {
    routing {
        // ...
        get("/") {
            call.respondRedirect("articles")
        }
        route("articles") {
            get {
                // Show a list of articles
            }
            get("new") {
                // Show a page with fields for creating a new article
            }
            post {
                // Save an article
            }
            get("{id}") {
                // Show an article with a specific id
            }
            get("{id}/edit") {
                // Show a page with fields for editing an article
            }
            post("{id}") {
                // Update or delete an article
            }
        }
    }
}
```

This code works as follows:
- The `get("/")` handler redirects all `GET` requests made to the `/` path to `/articles`.
- The `route("articles")` handler is used to [group routes](Routing_in_Ktor.md#multiple_routes) related to various actions: showing a list of articles, adding a new article, and so on. For example, a nested `get` function without a parameter responds to `GET` requests made to the `/articles` path, while `get("new")` responds to `GET` requests to `/articles/new`.



## Show a list of articles {id="list_articles"}

First, let's see how to show all articles when a user opens the `/articles` URL path. 

### Serve the templated content {id="serve_template"}

Open `com/example/plugins/Routing.kt` and add the following code to the `get` handler:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="3-13,21-24,61-63"}

The `call.respond` function accepts the `FreeMarkerContent` object that represents content to be sent to the client. 
In our case, the `FreeMarkerContent` constructor accepts two parameters:
* `template` is a name of a template loaded by the `FreeMarker` plugin. The `index.ftl` file doesn't exist yet, and we'll create it in the next chapter.
* `model` is a data model to be passed during template rendering. In our case, we pass an already [created list of articles](#model) in the `articles` template variable.


### Create a template {id="create_template"}

The FreeMarker plugin is [configured](#freemarker_config) to load templates located in the `templates` directory. First, create the `templates` directory inside `resources`. Then, create the `index.ftl` file inside `resources/templates` and fill it with the following content:

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

Let's examine the main pieces of this code:
- A comment with [@ftlvariable](https://www.jetbrains.com/help/idea/template-data-languages.html#special-comments) declares a variable named `articles` with the `List<Article>` type. This comment helps IntelliJ IDEA resolve attributes exposed by the `articles` template variable.
- The next part contains header elements of our journal - a logo and a heading.
- Inside the `list` tag, we iterate through all the articles and show their content. Note that the article title is rendered as a link to a specific article (the `/articles/${article.id}` path). A page showing a specific article will be implemented later in [](#show_article).
- A link at the bottom leads to `/articles/new` for [creating a new article](#new_article).

At this point, you can already [run](#run_app) the application and see the main page of our journal.



### Refactor a template {id="refactor_template"}

Given that a logo and a heading should be displayed on all pages of our application, let's refactor `index.ftl` and extract common code to a separate template. This can be accomplished using FreeMarker [macros](https://freemarker.apache.org/docs/ref_directive_macro.html). Create the `resources/templates/_layout.ftl` file and fill it with the following content:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/_layout.ftl"}

Then, update the `index.ftl` file to reuse `_layout.ftl`:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/index.ftl"}



## Create a new article {id="new_article"}

Now let's handle requests for the `/articles/new` path. Open `Routing.kt` and add the following code inside `get("new")`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="25-27"}

Here we respond with the `new.ftl` template without a data model since a new article doesn't exist yet.

Create the `resources/templates/new.ftl` file and insert the following content:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/new.ftl"}

The `new.ftl` template provides a form for submitting an article content. Given that this form sends data in a `POST` request to the `/articles` path, we need to implement a handler that reads form parameters and adds a new article to the [storage](#model). Go back to the `Routing.kt` file and add the following code in the `post` handler:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="28-35"}

The `call.receiveParameters` function is used to [receive form parameters](requests.md#form_parameters) and get their values. After saving a new article, `call.respondRedirect` is called to redirect to a page showing this article. Note that a URL path for a specific article contains an ID parameter whose value should be obtained at runtime. We'll take a look at how to handle path parameters in the next chapter. 


## Show a created article {id="show_article"}

To show a content of a specific article, we'll use the article ID as a [path parameter](Routing_in_Ktor.md#path_parameter). In `Routing.kt`, add the following code inside `get("{id}")`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="36-39"}

`call.parameters` is used to obtain the article ID passed in a URL path. To show the article with this ID, we need to find this article in a storage and pass it in the `article` template variable. 

Then, create the `resources/templates/show.ftl` template and fill it with the following code:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/show.ftl"}

The `/articles/${article.id}/edit` link at the bottom of this page should open a form for [editing or deleting](#edit_article) this article.


## Edit or delete an article {id="edit_article"}

A route for editing an article should look as follows:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="40-43"}

Similar to a route for showing an article, `call.parameters` is used to obtain the article identifier and find this article in a storage.

Now create `resources/templates/edit.ftl` and add the following code:

```html
```
{src="snippets/tutorial-website-interactive/src/main/resources/templates/edit.ftl"}

Given that HTML forms don't support `PATCH` and `DELETE` verbs, the page above contains two separate forms for editing and deleting an article. On the server side, we can distinguish `POST` requests sent by these forms by checking the input's `name` and `value` attributes.

Open the `Routing.kt` file and insert the following code inside `post("{id}")`:

```kotlin
```
{src="snippets/tutorial-website-interactive/src/main/kotlin/com/example/plugins/Routing.kt" lines="44-60"}

This code works as follows:
- `call.parameters` is used to obtain the ID of the article to be edited.
- `call.receiveParameters` is used to get the action initiated by a user - `update` or `delete`.
- Depending on the action, the article is updated or deleted from the storage.


> You can find the resulting project for this tutorial here: [tutorial-website-interactive](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/tutorial-website-interactive).



## Run the application {id="run_app"}

Let's see if our journal application is performing as expected. We can run our application by pressing the **Run** button next to `fun main(...)` in our `Application.kt`:

![Run Server](run-app.png){width="706"}

IntelliJ IDEA will start the application, and after a few seconds, we should see the confirmation that the app is running:

```Bash
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

Open [`http://localhost:8080/`](http://localhost:8080/) in a browser and try to create, edit, and delete articles:

![](ktor_journal.png){animated="true" width="502"}

However, if you stop the server, all saved articles vanish as we are storing them in an in-memory storage. In the next tutorial, we'll show you how to add persistence to the website using the [Exposed](https://github.com/JetBrains/Exposed) framework: [](interactive_website_add_persistence.md).
