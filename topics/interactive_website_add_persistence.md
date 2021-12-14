[//]: # (title: Adding persistence)

<microformat>
<var name="example_name" value="tutorial-website-interactive-persistence"/>
<include src="lib.xml" include-id="download_example"/>
<p>
Used libraries: <a href="https://github.com/JetBrains/Exposed">Exposed</a>, <a href="https://github.com/h2database/h2database">h2database</a>
</p>
</microformat>

<excerpt>Learn how to add persistence to a website using the Exposed ORM framework.</excerpt>




## What's next {id="whats_next"}

At this point, you should have gotten a basic idea of how to serve static files with Ktor and how the integration of plugins such as FreeMarker or kotlinx.html can enable you to write basic applications that can even react to user input.

### Feature requests {id="feature_requests"}

At this point, our journal application is still rather barebones, so of course, it might be a fun challenge to add more features to the project and learn even more about building interactive sites with Kotlin and Ktor. To get you started, here are a few ideas of how the application could still be improved, in no particular order:

- **Authentication!** Our current version of the journal allows all visitors to post content to our journal. We could use [Ktor's authentication plugins](authentication.md) to ensure that only select users can post to the journal while keeping read access open to everyone.
- **Make it look nicer!** The stylesheets for the journal are currently rudimentary at best. Consider creating your own style sheet and serving it as a static `.css` file from Ktor!
- **Organize your routes!** As the complexity of our application increases, so does the number of routes we try to support. For bigger applications, we usually want to add structure to our routing – like separating routes out into separate files. If you'd like to learn about different ways to organize your routes with Ktor, check out the [](Routing_in_Ktor.md) help topic.