[//]: # (title: Adding interactivity using templates)


<excerpt>Learn how to create an interactive website using HTML templating engines like FreeMarker.</excerpt>


## Home page with templates {id="home_page_templates"}

It's time to build the main page of our journal, which is in charge of displaying multiple journal entries. We will create this page with the help of a *template engine*. Template engines are quite common in web development, and Ktor supports a [variety of them](Working_with_views.md). In our case, we're going to choose [FreeMarker](https://freemarker.apache.org/).

### Adjust FreeMarker configuration {id="freemarker_config"}

The Ktor plugin for IntelliJ IDEA already [generated code](#source_code) for the FreeMarker plugin in the `plugins/Templating.kt` file:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Templating.kt" lines="8-10,12-13"}

The `templateLoader` setting tells our application that FreeMarker templates will be located in the `templates` directory. Let's also add the `outputFormat` as follows:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Templating.kt" lines="3-13"}

The `outputFormat` setting helps convert control characters provided by the user to their corresponding HTML entities. This ensures that when one of our journal entries contains a String like `<b>Hello</b>`, it is actually printed as `<b>Hello</b>`, not **Hello**. This so-called [escaping](https://freemarker.apache.org/docs/dgui_misc_autoescaping.html) is an essential step in preventing [XSS attacks](https://owasp.org/www-community/attacks/xss/).


### Write the journal template {id="journal_template"}

Our journal main page should contain everything we need for a basic overview of our journal: a title, header image, a list of journal entries, and a form for adding new journal entries. To define this page layout, we use the FreeMarker Template Language (`ftl`), which contains our HTML source as well as FreeMarker variable definitions and instructions on how to use the variables in the context of the page.

Let's create a new `templates` directory inside `resources`. Inside this directory, we'll create a file called `index.ftl` and fill it with the following content:

```html
```
{src="snippets/tutorial-website/src/main/resources/templates/index.ftl"}

As you can see, we are using FTL syntax to define, access, and iterate over variables. The variable `entries` which we loop over has the type `kotlin.collections.List<com.example.BlogEntry>`. This is simply the fully-qualified name of a Kotlin `List<BlogEntry>`. However, we haven't created the `BlogEntry` class yet, but now that we are referencing it, it seems like a good time to fix that!

> If you'd like to learn more about FreeMarker's syntax, check out [their official documentation](https://freemarker.apache.org/docs/dgui_quickstart.html).

### Define a model for the journal entries {id="define_model"}

As defined by our usage in the FreeMarker template, the `BlogEntry` class needs two attributes: a `headline` and a `body`, both of type `String`. Let's create a file named `BlogEntry.kt` in the `com.example` package, and fill it with the corresponding Kotlin data class, which can then be injected into the template:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/BlogEntry.kt" lines="1-3"}

It would also make sense if we defined a temporary storage for our entries. We can do this in the same file as a top-level declaration:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/BlogEntry.kt" lines="5-8"}

At this point, we have defined a template and the model that will be used for rendering it. Now, Ktor just needs to pass our stored journal entries and serve the resulting page.

### Serve the templated content {id="serve_template"}

The overview page we just templated is the center point of our application. So, it would make sense to make it available under the `/` route. Let's add a route for it to the `routing` block inside our `Application.configureRouting()`. Open the `plugins/Routing.kt` file and add the code below:


```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="19-21"}

We can now run the application. Opening [`http://localhost:8080/`](http://localhost:8080/) in a browser, we should see our header image, headline and subtitle, and a list of journal entries (well, just one for now) alongside a form for submitting new entries:

![FreeMarker Browser Output](main_page.png){width="706"}

It looks like our display logic is working just fine! Now, we only need to make the **Submit** button work, and we'll be able to view and add new entries for our journal!


## Submit a form {id="submit_form"}

The `<form>` we defined in the previous chapter sends a `POST` request to `/submit` containing the `headline` and `body` of our new journal entry. Let's make our application correctly consume this information and submission of new journal entries! We define a handler for the `/submit` route inside our `Application.configureRouting()`'s `routing` block like this:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="22-27,47"}

[receiveParameters](requests.md#form_parameters) allows us to parse form data (for both `urlencoded` and `multipart`). We then extract the `headline` and `body` fields from the form, ensuring they are both not null, and create a new `BlogEntry` object from this information, adding it to the beginning of our `blogEntries` list.

For more detailed information on the fancy features that are available in the context of Ktor's request model, check out the [](requests.md) topic.

To show the user that the submission was successful, we still want to send back a bit of HTML. We could re-use our knowledge and create a FreeMarker template for this "Success" page as well – but to cover some more Ktor functionality, we will try an alternative approach instead. When we autocomplete on the `call` object, we can see that Ktor allows us to respond to requests using a variety of functions:

![Respond](respond.png){width="658"}

Amongst these is `call.respondHtml`. This function allows us to craft our HTML response using Kotlin syntax, thanks to Ktor's integration with [kotlinx.html](html_dsl.md) (a [DSL](https://kotlinlang.org/docs/type-safe-builders.html) designed for seamlessly combining Kotlin code and HTML-like tags).

At the bottom of the `post("/submit")` handler block, let's add the following code:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="28-46"}

Notice how this code combines Kotlin-specific logic (like `count`ing the entries or using string interpolation) with HTML-like syntax!

To test the route, let's re-run our application, navigate to [`http://localhost:8080/`](http://localhost:8080/), and submit a new entry. If everything has gone according to play, we'll now see the HTML page.

![Static HTML](submit.png){width="706"}

## We're done! {id="done"}

It's time to pat ourselves on the back – we've put together a nice little journal application, and have learned about many topics in the meantime. From static files to templating, from basic routing to kotlinx.html, we've covered a lot of ground.

![](ktor_journal.png){animated="true" width="706"}

This concludes the guided part of this tutorial. We have included the final state of the journal application in the [codeSnippets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets) project: [tutorial-website](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-website). But of course, your journey doesn't have to stop here. Check out the [What's next](#whats_next) section to get an idea of how you could expand the application, and where to go if you need help in your endeavors!


## What's next {id="whats_next"}

At this point, you should have gotten a basic idea of how to serve static files with Ktor and how the integration of plugins such as FreeMarker or kotlinx.html can enable you to write basic applications that can even react to user input.

### Feature requests {id="feature_requests"}

At this point, our journal application is still rather barebones, so of course, it might be a fun challenge to add more features to the project and learn even more about building interactive sites with Kotlin and Ktor. To get you started, here are a few ideas of how the application could still be improved, in no particular order:

- **Make it consistent!** You would usually not mix FreeMarker and kotlinx.html – we've taken some liberty here to explore more than one way of structuring your application. Consider powering your whole journal with kotlinx.html or FreeMarker, and make it consistent!
- **Authentication!** Our current version of the journal allows all visitors to post content to our journal. We could use [Ktor's authentication plugins](authentication.md) to ensure that only select users can post to the journal while keeping read access open to everyone.
- **Persistence!** Currently, all our journal entries vanish when we stop our application, as we are only storing them in a variable. You could try integrating your application with a database like PostgreSQL or MongoDB, using one of the plenty projects that allow database access from Kotlin, like [Exposed](https://github.com/JetBrains/Exposed) or [KMongo](https://litote.org/kmongo/).
- **Make it look nicer!** The stylesheets for the journal are currently rudimentary at best. Consider creating your own style sheet and serving it as a static `.css` file from Ktor!
- **Organize your routes!** As the complexity of our application increases, so does the number of routes we try to support. For bigger applications, we usually want to add structure to our routing – like separating routes out into separate files. If you'd like to learn about different ways to organize your routes with Ktor, check out the [](Routing_in_Ktor.md) help topic.
