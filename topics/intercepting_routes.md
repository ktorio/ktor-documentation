[//]: # (title: Intercepting Routes)

<include src="lib.md" include-id="outdated_warning"/>

When routing node is selected, the routing system builds a special pipeline to execute the node.
This pipeline consists of handler(s) for the selected node and any interceptors installed into nodes that
constitutes path from root to the selected node in order from top to bottom.

```kotlin
route("/portal") {
   route("articles") { … }
   route("admin") {
      intercept(ApplicationCallPipeline.Features) { … } // verify admin privileges
      route("article/{id}") { … } // manage article with {id}
      route("profile/{id}") { … } // manage profile with {id}
   }
}
```

Given the routing tree above, when request URI starts with `/portal/articles`, routing will handle
call normally, but if the request is in `/portal/admin` section, it will first execute interceptor to validate
if the current user has enough privilege to access admin pages.

Other examples could be installing JSON serialisation into `/api` section,
loading user from the database in `/user/{id}` section and placing it into call's attributes, etc. 


