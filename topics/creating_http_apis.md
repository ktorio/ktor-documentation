[//]: # (title: Creating HTTP APIs)

<microformat>
<p>
<a href="https://github.com/kotlin-hands-on/creating-http-api-ktor/">Template project</a>
</p>
<p>
<a href="https://github.com/kotlin-hands-on/creating-http-api-ktor/tree/final">Final project</a>
</p>
</microformat>

In this hands-on, we're going to create an HTTP API using Kotlin and Ktor that can serve as a backend for any application, be it mobile, web, desktop, or even a B2B service. We will see how routes are defined and structured, how serialization features help with simplifying tedious tasks, and how we can test parts of our application both manually and automated.

## What we will build

Throughout the hands-on, we'll build a simple JSON API that allows us to query information about the customers of our fictitious business, as well as the orders we currently want to fulfill.

We will build a convenient way of listing all customers & orders in our system, get information for individual customers & orders, and provide functionality to add new entries and remove old entries.

We will be using two ways to define routes and organize these by files. They certainly aren't the only ways to define routes in applications, but they showcase differently maintainable approaches. For other styles and options check out the [Routing in Ktor](Routing_in_Ktor.md) help topic.

You can find the [template project](https://github.com/kotlin-hands-on/creating-http-api-ktor/) as well as the source code of the [final](https://github.com/kotlin-hands-on/creating-http-api-ktor/tree/final) application on the corresponding GitHub repository.


## Project setup

If we were to start a fresh idea from zero, Ktor would have a few ways of setting up a preconfigured Gradle project: [start.ktor.io](https://start.ktor.io/) and the [Ktor IntelliJ IDEA plugin](intellij-idea.xml) make it easy to create a starting-off point for projects using a variety of features from the framework.

For this tutorial, however, we have made a starter template available that includes all configuration and required dependencies for the project.

[Please clone the project repository from GitHub, and open it in IntelliJ IDEA.](https://github.com/kotlin-hands-on/creating-http-api-ktor/)

The template repository contains a basic Gradle projects for us to build our project. Because it already contains all dependencies that we will need throughout the hands-on, **you don't need to make any changes to the Gradle configuration.**

It is still beneficial to understand what artifacts are being used for the application, so let's have a closer look at our project template and the dependencies and configuration it relies on.

### Dependencies

For this hands-on, the `dependencies` block in our `build.gradle` file is probably the most interesting part:

```groovy
dependencies {
    implementation "io.ktor:ktor-server-core:$ktor_version"
    implementation "io.ktor:ktor-server-netty:$ktor_version"
    implementation "ch.qos.logback:logback-classic:$logback_version"
    implementation "io.ktor:ktor-serialization:$ktor_version"
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$kotlin_serialization"

    testImplementation "io.ktor:ktor-server-tests:$ktor_version"
}
```
Let's briefly go through these dependencies one-by-one:

- `ktor-server-core` adds Ktor's core components to our project.
- `ktor-server-netty` adds the [Netty](https://netty.io/) engine to our project, allowing us to use server functionality without having to rely on an external application container.
- `logback-classic` provides an implementation of [SLF4J](http://www.slf4j.org/), allowing us to see nicely formatted logs in our console.
- `ktor-serialization` provides a convenient mechanism for converting Kotlin objects into a serialized form like JSON, and vice versa. We will use it to format our APIs output, and to consume user input that is structured in JSON. In order to use `ktor-serialization`, we also need the `kotlinx-serialization-runtime-common` dependency, and have to apply the `org.jetbrains.kotlin.plugin.serialization` plugin.
- `ktor-server-tests` allows us to test parts of our Ktor application without having to use the whole HTTP stack in the process. We will use this to define unit tests for our project.

### Configurations: application.conf and logback.xml

The repository also includes a basic `application.conf` in [HOCON](https://en.wikipedia.org/wiki/HOCON) format, located in the `resources` folder. Ktor uses this file to determine the port on which it should run, and it also defines the entry point of our application. If you'd like to learn more about how a Ktor server is configured, check out the [](Configurations.xml) help topic.

Also included in the same folder is a `logback.xml` file, which sets up the basic logging structure for our server. If you'd like to learn more about logging in Ktor, check out the [](logging.md) topic.

### Entry point

Our `application.conf` configures the entry point of our application to be `com.jetbrains.handson.website.ApplicationKt.module`. This corresponds to the `Application.module()` function in `Application.kt`, which currently doesn't do anything:

```kotlin
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

}
```

The entry point of our application is important because we install Ktor's features and define routing for our API here – something we can start with right now! 


## Customer routes

First, let's tackle the `Customer` side of our application. We need to create a model which defines the data that's associated with a customer. We also need to create a series of endpoints to allow Customers to be added, listed, and deleted.

### The `Customer` model

For our case, a customer should store some basic information in the form of text: A customer should have an `id` by which we can identify them, a first and last name, and an email address. An easy way to model this in Kotlin is by using a data class.

Create a file name `Customer.kt` in a new package named `models` and add the following:

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: String, val firstName: String, val lastName: String, val email: String)
```

Note that we are using the `@Serializable` annotation from [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization). Together with its Ktor integration, this will allow us to generate the JSON representation we need for our API responses automatically – as we will see in just a bit.

First, we need to define a place to put all these potential customers.

### Storing customers

To not complicate the code, for this tutorial we'll be using an in-memory storage (i.e. a mutable list of `Customer`s) – in a real application, we would be storing this information in a database, so that it doesn't get lost after restarting our application. We can simply add this line to the top level of the `Customer.kt` file:

```kotlin
val customerStorage = mutableListOf<Customer>()
```

Now that we have a well-defined `Customer` class and a storage for our customer objects, it's time we create endpoints and expose them via our API!

### Defining the routing for customers

We want to respond to `GET`, `POST`, and `DELETE` requests on the `/customer` endpoint. As such, let's define our routes with the corresponding HTTP methods. Create a file called `CustomerRoutes.kt` in a new package called `routes`, and fill it with the following:

```kotlin
import io.ktor.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get {

        }
        get("{id}") {

        }
        post {

        }
        delete("{id}") {

        }
    }
}
```

In this case, we're using the `route` function to group everything that falls under the `/customer` endpoint. We then create a block for each HTTP method. This is just one approach how we can structure our routes – when we tackle the `Order` routes in the next chapter, we will see another approach.

Notice also how we actually have two entries for `get`: one without a route parameter, and the other with `{id}`. We'll use the first entry to list all customers, and the second to display a specific one.

#### Listing all customers

To list all customers, we can simply return the `customerStorage` list by using the `call.respond` function in Ktor. which can take a Kotlin object and return it serialized in a specified format. For the `get` handler, it looks like this:

```kotlin
get {
    if (customerStorage.isNotEmpty()) {
        call.respond(customerStorage)
    } else {
        call.respondText("No customers found", status = HttpStatusCode.NotFound)
    }
}
```

In order for this to work, we need to enable [content negotiation](serialization.md) in Ktor. What does content negotiation do? Let us consider the following request:

```http request
GET http://0.0.0.0:8080/customer
Accept: application/json
```

When a client makes such a request, content negotiation allows the server to examine the `Accept` header, see if it can serve this specific type of content, and if so, return the result.

In our case, we're going to install the `ContentNegotiation` feature and enable its support for JSON. Let's add the following code to the `Application.module()` function:

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
}
```

JSON support is powered by `kotlinx-serialization`. We previously used its annotation `@Serializable` to annotate our `Customer` data class, meaning that Ktor now knows how to serialize `Customer`s (and collections of `Customer`s!)

#### Returning a specific customer

Another route we want to support is one that returns a specific customer based on their ID (in this case, `200`):

```http request
GET http://0.0.0.0:8080/customer/200
Accept: application/json
```

In Ktor, paths can also contain [parameters](Routing_in_Ktor.md#match_url) that match specific path segments. We can access their value using the indexed access operator (`call.parameters["myParamName"]`). Let's add the following code to the `get("{id}")` entry:


```kotlin
get("{id}") {
    val id = call.parameters["id"] ?: return@get call.respondText(
        "Missing or malformed id",
        status = HttpStatusCode.BadRequest
    )
    val customer =
        customerStorage.find { it.id == id } ?: return@get call.respondText(
            "No customer with id $id",
            status = HttpStatusCode.NotFound
        )
    call.respond(customer)
}
```

First, we check whether the parameter `id` exists in the request. If it does not exist, we respond with a 400 "Bad Request" status code and an error message, and are done. If the parameter exists, we try to `find` the corresponding record in our `customerStorage`. If we find it, we'll respond with the object. Otherwise, we'll return a 404 "Not Found" status code with an error message.

Note that while we return a 400 "Bad request" when the `id` is null, this case should actually never be encountered. Why? Because this would only happen if no parameter `{id}` was passed in – but in this case, the route we defined previously would already handle the request.

#### Creating a customer

Next, we implement the option for a client to `POST` a JSON representation of a client object, which then gets put into our customer storage. Its implementation looks like this:

```kotlin
post {
    val customer = call.receive<Customer>()
    customerStorage.add(customer)
    call.respondText("Customer stored correctly", status = HttpStatusCode.Accepted)
}
```

`call.receive` integrates with the Content Negotiation feature we configured one of the previous sections. Calling it with the generic parameter `Customer` automatically deserializes the JSON request body into a Kotlin `Customer` object. We can then add the customer to our storage and respond with a status code of 201 "Accepted".

At this point, it is worth highlighting again that in this tutorial, we are also intentionally glancing over issues that could arise from e.g. multiple requests accessing the storage at the same time. In production, data structures and code that can be accessed from multiple requests / threads at the same time should account for these cases – something that is out of the scope of this hands-on.

#### Deleting a customer

The implementation for deleting a customer follows a similar procedure as we have used for listing a specific customer. We first get the `id` and then modify our `customerStorage` accordingly:

```kotlin
delete("{id}") {
    val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
    if (customerStorage.removeIf { it.id == id }) {
        call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
    } else {
        call.respondText("Not Found", status = HttpStatusCode.NotFound)
    }
}
```

Similar to the definition of our `get` request, we make sure that the `id` is not null. If the `id` is absent, we respond with a 400 "Bad Request" error.

### Registering the routes {id="register-customer-routes"}

Up until now, we have only defined our routes inside an extension function on `Route` – so Ktor doesn't know about our routes yet, and we need to register them. While we could certainly add each route directly in `Application.module` inside a `routing` block, it's more maintainable to group route registration in the corresponding file. We then just call the corresponding function to register all of them. Once we look at our implementation for `Orders`, this will hopefully be even more apparent.

Let's add the following code to our `CustomerRoutes.kt` file:

```kotlin
fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}
```

Now we just need to invoke this function in our `Application.module()` function in `Application.kt`:

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
}
```

We've now completed the implementation for the customer-related routes in our API. If you would like to validate that everything works right away, you can skip ahead to the chapter about _Manually testing HTTP endpoints_. If you can still bear the suspense, we can move on to the implementation of order-related routes.


## Order routes

Now that we have API endpoints for `Customer`s done, let's move on to `Orders`. While some of the implementation is rather similar, we will be using a different way of structuring our application routes, and include routes that sum up the price of individual items in an order.


### Defining the model

The orders we want to store in our system should be identifiable by an order number (which might contain dashes), and should contain a list of order items. These order items should have a textual description, the number how often this item appears in the order, as well as the price for the individual item (so that we can compute the total price of an order on demand).

We create a new file called `Order.kt` and fill it with the definition of the two data classes:

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Order(val number: String, val contents: List<OrderItem>)

@Serializable
data class OrderItem(val item: String, val amount: Int, val price: Double)
```

We also once again need a place to store our orders. To skip having to define a `POST` route – something you're more than welcome to attempt on your own using the knowledge from the `Customer` routes – we will prepopulate our `orderStorage` with some sample orders. We can again define it as a top-level declaration inside the `Order.kt` file.

```kotlin
val orderStorage = listOf(Order(
    "2020-04-06-01", listOf(
        OrderItem("Ham Sandwich", 2, 5.50),
        OrderItem("Water", 1, 1.50),
        OrderItem("Beer", 3, 2.30),
        OrderItem("Cheesecake", 1, 3.75)
    )),
    Order("2020-04-03-01", listOf(
        OrderItem("Cheeseburger", 1, 8.50),
        OrderItem("Water", 2, 1.50),
        OrderItem("Coke", 2, 1.76),
        OrderItem("Ice Cream", 1, 2.35)
    ))
)
```

### Defining order routes
We respond to a set of `GET` requests with three different patterns:



```http request
GET http://0.0.0.0:8080/order/
Content-Type: application/json

GET http://0.0.0.0:8080/order/{id}
Content-Type: application/json

GET http://0.0.0.0:8080/order/{id}/total
Content-Type: application/json
```

The first will return all orders, the second will return an order given the `id`, and the third will return the total of an order (prices of individual `OrderItems` multiplied by number of each item).

With orders, we're going to follow a different pattern when it comes to defining routes.
Instead of grouping all routes under a single `route` function with different
HTTP methods, we'll use individual functions.

#### Listing all and individual orders

For listing all orders, we'll follow the same pattern as with customers – the difference
being that we're defining it in its own function. Let's create a file called `OrderRoutes.kt` inside the `routes` package, and start with the implementation of the route inside a function called `listOrdersRoute()`.

```kotlin
fun Route.listOrdersRoute() {
    get("/order") {
        if (orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        }
    }
}
```

We apply the same structure to individual orders – with a similar implementation to customers, but encapsulated in its own function:

```kotlin
fun Route.getOrderRoute() {
    get("/order/{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}
```

#### Totalizing an order

Getting the total amount of an order consists of iterating over the items of an order and
totalizing this. Implemented as a `totalizeOrderRoute` function, it looks like this, which besides the summing process should already look familiar:

```kotlin
fun Route.totalizeOrderRoute() {
    get("/order/{id}/total") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val total = order.contents.map { it.price * it.amount }.sum()
        call.respond(total)
    }
}
```

A small thing to note here is that we are not limited to suffixes of routes for parameters – as we can see, it's absolutely possible to have a section in the middle be a route parameter (`/order/{id}/total`).

### Registering the routes {id="register-order-routes"}

Finally, much like the case of customers, we need to register the routes. Hopefully, this makes it clear why grouping routes makes more sense as the number of routes grow. Still in `OrderRoutes.kt`, we add an `Application` extension function called `registerOrderRoutes`:

```kotlin
fun Application.registerOrderRoutes() {
    routing {
        listOrdersRoute()
        getOrderRoute()
        totalizeOrderRoute()
    }
}
```

We then add the function call in our `Application.module()` function in `Application.kt`:

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
    registerOrderRoutes()
}
```

Now that we have everything wired up, we can finally start testing our application, and see if everything works as we would expect it to!


## Manually testing HTTP endpoints

Now that we have all the endpoints ready, it's time to test our application. While
we can use any browser to test `GET` requests, we'll need a separate tool to test the other HTTP methods. Some options are `curl` or Postman – but if you're using [IntelliJ IDEA Ultimate Edition](https://www.jetbrains.com/idea/), you actually already have a client that supports `.http` files, allowing you to specify and execute requests – without even having to leave the IDE.

### Creating a customer HTTP test file

`.http` files are one way of specifying HTTP requests to be executed by different types of tools, including IntelliJ IDEA Ultimate Edition. Let's create a new directory `test` under `src` of our project; inside let's create a file called `CustomerTest.http` and enter the following contents:

```http request
POST http://127.0.0.1:8080/customer
Content-Type: application/json

{
  "id": "100",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@company.com"
}


###
POST http://127.0.0.1:8080/customer
Content-Type: application/json

{
  "id": "200",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@company.com"
}

###
POST http://127.0.0.1:8080/customer
Content-Type: application/json

{
  "id": "300",
  "firstName": "Mary",
  "lastName": "Smith",
  "email": "mary.smith@company.com"
}


###
GET http://127.0.0.1:8080/customer
Accept: application/json

###
GET http://127.0.0.1:8080/customer/200

###
GET http://127.0.0.1:8080/customer/500

###
DELETE http://127.0.0.1:8080/customer/100

###
DELETE http://127.0.0.1:8080/customer/500
```

Inside this file, we have now specified a bunch of HTTP requests, using all the supported HTTP methods of our API. IntelliJ IDEA now allows us to run each of these requests individually or all together. To really see what's going on, let's run them individually. But first, we need to make sure our API is actually reachable!

### Running our API server

Before we can run a request, we need to first start our API server. The easiest
way to do this is to use IntelliJ IDEA and click on the Run icon in the gutter:

![Run Server](run-app.png){width="965"}

Once the server is up and running, we can execute each request by pressing Alt+Enter or by using the Run icon in the gutter:

![Run POST Request](run-post-request.png){width="965"}

If everything is correct, we should see the output in the Run tool window:

![Run Output](run-output.png){width="968"}

### Order endpoints

For the order endpoints we can follow the same procedure: we create a new file called `OrderTest.http` in the `test` directory of our project, and fill it with some HTTP requests:

```http request
GET http://127.0.0.1:8080/order/2020-04-06-01
Content-Type: application/json

###
GET http://127.0.0.1:8080/order/2020-04-06-01/total
Content-Type: application/json
```

Running these requests just as the ones before, we should see the expected output – detailed information about one order, and the total of the order respectively.


## Automated testing

While manual testing is great and necessary, it also makes sense to have automated testing of endpoints.

Thanks to `ktor-server-tests`, Ktor allows us to test endpoints without having to start up the entire underlying engine (such as Netty). The framework ships with a few helper methods for running
tests requests, one significant one being `withTestApplication`.

Let's write a unit test to ensure that our order route returns properly formatted JSON content. We create a new file under `test/kotlin` called `OrderTests.kt` and add the following code:

```kotlin
class OrderRouteTests {
    @Test
    fun testGetOrder() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/order/2020-04-06-01").apply {
                assertEquals(
                    """{"number":"2020-04-06-01","contents":[{"item":"Ham Sandwich","amount":2,"price":5.5},{"item":"Water","amount":1,"price":1.5},{"item":"Beer","amount":3,"price":2.3},{"item":"Cheesecake","amount":1,"price":3.75}]}""",
                    response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
```

By using `withTestApplication`, we're indicating to our application that we want to run it as a test. Using the `handleRequest` helper method (also shipped as part of Ktor), we define request to a specific endpoint, in this case `/order/{id}`.

Note that since our string contains a lot of quotation marks around keys and values (like `"number"`), this is a great place to use [raw strings](https://kotlinlang.org/docs/basic-types.html#string-literals) using triple-quotes (`"""`), saving us the hassle of individually escaping every special character inside the string.

If we try and compile this code however, it won't work. This is due to the parameter being passed to our application (`testing = true`). For this to work, we need to add the corresponding parameter to our application:

```kotlin
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        json()
    }
    registerCustomerRoutes()
    registerOrderRoutes()
}
```

With this, we can now run our unit test from the IDE and see the results. Much like we've done for this endpoint, we can add all other endpoints as tests and automate the testing of our HTTP API.

And just like that, we have finished building our small JSON-based HTTP API. Of course, there are tons of topics you can still explore around Ktor and building APIs with it, so your learning journey doesn't have to stop here!


## What's next

With this step, we've finalized our HTTP API application. From here on we can add other
features such as Authentication, etc.

### Feature requests

- **Authentication**: currently, the API is open to whomever would like to access it. If you want to restrict access, have a look at Ktor's support for [JWT](jwt.md) and other authentication methods.

- **Learn more about route organization!** If you'd like to learn about different ways to organize your routes with Ktor, check out the [](Routing_in_Ktor.md) topic.

- **Persistence!** Currently, all our journal entries vanish when we stop our application, as we are only storing them in a variable. You could try integrating your application with a database like PostgreSQL or MongoDB, using one of the plenty projects that allow database access from Kotlin, like [Exposed](https://github.com/JetBrains/Exposed) or [KMongo](https://litote.org/kmongo/).

- **Integrate with a client!** Now that we are exposing data, it would make sense to explore how this data can be consumed again! Try writing an API client using the [Ktor HTTP client](client.md), for example, or try accessing it from a website using JavaScript or Kotlin/JS!

  To make sure your API works nicely with **browser clients**, you should also set up a policy for [Cross-Origin Resource Sharing (CORS)](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS). The simplest and most permissive way to do this with Ktor would be by adding the following snippet to the top of `Application.module()`:

  ```kotlin
  install(CORS) {
      anyHost()
  }
  ```

### Learning more about Ktor

On this page, you will find a set of hands-on tutorials that also focus more on specific parts of Ktor. For in-depth information about the framework, including further demo projects, check out [ktor.io](https://ktor.io/).

### Community, help and troubleshooting

To find more information about Ktor, check out the official website. If you run into trouble, check out the [Ktor issue tracker](https://youtrack.jetbrains.com/issues/KTOR) – and if you can't find your problem, don't hesitate to file a new issue.

You can also join the official [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up). We have channels for `#ktor` and more available, and a helpful community that supports each other for Kotlin related problems.
