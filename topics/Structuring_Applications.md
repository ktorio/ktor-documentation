[//]: # (title: Application structure)

One of Ktor’s strong points is in the flexibility it offers in terms of structuring our application. Different to many other server-side frameworks, it doesn't force us into a specific pattern such as having to place all cohesive routes in a single class name `CustomerController` for instance. While it is certainly possible, it's not required.

In this section we're going to examine the different options we have to structure our applications.

## Grouping by file

One approach is to group routes that are related in a single file. If our application is dealing with Customers and Orders for instance, this would mean having a `CustomerRoutes.kt` and an `OrderRoutes.kt` file:

<tabs>
<tab title="CustomerRoutes.kt">

```kotlin
fun Route.customerByIdRoute() {
    get("/customer/{id}") {

    }
}

fun Route.createCustomerRoute() {
    post("/customer") {

    }
}
```
</tab>
<tab title="OrderRoutes.kt">

```kotlin
fun Route.orderByIdRoute() {
    get("/order/{id}") {
    
    }
}

fun Route.createOrderRoute() {
    post("/order") {

    }
}
```
</tab>
</tabs>


What would happen with sub-routes? Such as order/shipment for instance? It somewhat depends on what we understand by this URL. 
If we’re talking about these as resources (which they are), shipment itself could therefore be a resource, and could easily map 
to another file `ShipmentRoutes.kt`.

## Grouping routing definitions

One advantage of this approach is that we can also group the routing definitions, and potentially functionality, per file. For instance, let’s assume 
that we follow the group per file layout as above. Even though are routes in a different files, we need to declare them at Application level. As such 
our app would look something like the following:

```kotlin
routing {
    customerByIdRoute()
    createCustomerRoute()
    orderByIdRoute()
    createOrderRoute()
}
```

If we have tons of routes in our app, this could quickly become long and cumbersome. Since we have however routes grouped by file, 
we can take advantage of this and define the routing in each file also. For this we could create an extension for [Application](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.application/-application/index.html) and define the routes:

<tabs>
<tab title="CustomerRoutes.kt">

```kotlin
fun Application.customerRoutes() {
    routing {
        customerByIdRoute()
        createCustomerRoute()    
    }    
}
```
</tab>
<tab title="OrderRoutes.kt">

```kotlin
fun Application.orderRoutes() {
    routing {
        orderByIdRoute()
        createOrderRoute()
    }
}
```
</tab>
</tabs>



Now in our actual `Application.module` startup, we’d simply call these functions, without the need for the `routing` block:

```kotlin
fun Application.module() {
    // Init....
    customerRoutes()
    orderRoutes()
}
```

We can even take this one step further - install plugins per application, as needed, especially for instance when we’re using 
the Authentication plugin which depends on specific routes. One important note however is that Ktor will detect if a 
plugin has been installed twice by throwing an [DuplicateApplicationFeatureException](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.application/-duplicate-application-feature-exception/index.html) exception.

### A note on using objects

Using objects to group routing functions doesn’t provide any kind of performance or memory benefits, as top-level functions in Ktor are 
instantiated a single time. While it can provide some sort of cohesive structure where we may want to share common functionality, it isn’t 
necessary to use objects in case we’re worried about any kind of overhead.

## Grouping by folders

Having everything in a single file can become a bit cumbersome as the file grows. 
What we could do instead is use folders (i.e. packages) to define different areas and then have each route in its own file.

![Grouping by folders](ktor-routing-1.png){width="316"}

While this does provide the advantage of a nice layout when it comes to routes and the individual actions, it could certainly 
lead to “package overload”, and potentially having tons of filenames named the same, making navigation somewhat more difficult.
 On the other hand, as we’ll see in the next example, we could also merely prefix each file with area (i.e. `CustomerCreate.kt` for instance).

## Grouping by features

Frameworks such as ASP.NET MVC or Ruby on Rails, have the concept of structuring applications using three folders - Model, View, and Controllers (Routes).

![Model View Controller](ktor-routing-2.png){width="382"}


This isn’t far-fetched with the schema we have above which is grouping routes in their own packages/files, our views in the `resources` folder in the case of Ktor, and of course, nothing prevents us from having a package model where we place any data we want to display or respond to HTTP endpoints with.

While this approach may work and is similar to other frameworks, some would argue that it would make more sense to group things by features, i.e. instead of having the project 
distributed by routes, models and views, have these groups by specific behaviour/features, i.e. `OrderProcessPayment`, `CustomerAddressChange`, etc.

![Feature grouping](ktor-routing-3.png){width="381"}

With many frameworks, this kind of organization of code isn’t viable without seriously hacking the underlying conventions. However, with Ktor, given how flexible it is, 
in principle it shouldn’t be a problem. With one caveat - when we’re using a [template engine](Working_with_views.md), resources could be an issue. But let’s see how we could solve this.

How this problem is solved very much depends on what is used for Views. If our application is merely an HTTP backend and we’re using client-side technology, then usually all rendering is 
client-side. If we’re using Kotlinx.HTML, then once again it’s not an issue as the page can be generated from any Kotlin file placed anywhere. 

The issue arises more when we’re using a templating engine such as FreeMarker. These are peculiar in how and where template files should be located. 
Fortunately some of them offer flexibility in how templates are loaded.

For instance with FreeMarker, we can use a MultiTemplateLoader and then have templates loaded from different locations:

```kotlin
install(FreeMarker) {
    val customerTemplates = FileTemplateLoader(File("./customer/changeAddress"))
    val loaders = arrayOf<TemplateLoader>(customerTemplates)
    templateLoader = MultiTemplateLoader(loaders)
}
```

Obviously this code isn’t ideal as it uses relative paths amongst other things, but it’s not hard to see how we could actually have 
this loop through folders and load templates, or even have a custom build action that copies views to our `resources` folder prior to execution. 
There are quite a number of ways to solve the issue.

The benefit of this approach is that we can group everything related to the same functionality in a single location, by feature, as opposed to 
the technical/infrastructure aspect of it.