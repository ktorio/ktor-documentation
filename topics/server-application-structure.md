[//]: # (title: Routing structure)

<link-summary>
Learn how to organize your routing code to keep it maintainable as your application grows.
</link-summary>

One of Ktor's strong points is its flexibility and that it does not enforce a specific routing structure. Instead, you
can organize routes in a way that best fits the size and complexity of your application.

This page describes common strategies for splitting routing logic into reusable functions, grouping routes by feature,
and keeping routing definitions maintainable.

## Group by file {id="group_by_file"}

One approach is to define routing logic as extension functions on `Route`. This allows you to keep route handlers
modular and easy to reuse.

For example, if your application is managing customers and orders, you could split the routing logic between 
`CustomerRoutes.kt` and `OrderRoutes.kt` files:

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
fun Route.getOrderRoute() {
    get("/order/{id}") {

    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id}/total") {

    }
}
```
</tab>
</tabs>


## Registering routes from multiple sources {id="group_routing_definitions"}

You can register routes from different files or modules by calling their extension functions inside one or more
routing blocks.

<tabs>
<tab title="CustomerRoutes.kt">

```kotlin
fun Application.customerRoutes() {
    routing {
        listCustomersRoute()
        customerByIdRoute()
        createCustomerRoute()
        deleteCustomerRoute()
    }    
}
```
</tab>
<tab title="OrderRoutes.kt">

```kotlin
fun Application.orderRoutes() {
    routing {
        listOrdersRoute()
        getOrderRoute()
        totalizeOrderRoute()
    }
}
```
</tab>
</tabs>


You can then call these functions without the need for the `routing` block:

```kotlin
fun Application.module() {
    // Init....
    customerRoutes()
    orderRoutes()
}
```

In multi-module projects, feature modules can expose routing definitions that the application imports and registers:

```kotlin
import com.example.customer.customerRoutes
import com.example.order.orderRoutes
```

This pattern helps keep routing modular and enables clean separation of features.

## Organizing routes by package {id="organize-by-package"}

As a project grows, grouping code by package improves discoverability.
You can place route functions in packages that reflect the domain structure:

```generic
routes/
    customer/
        CustomerRoutes.kt
        Create.kt
        Details.kt
    order/
        OrderRoutes.kt
        Shipment.kt
```

This approach keeps related routing logic close together and avoids large files.

## Organizing routes by feature {id="organize-by-feature"}

Medium and large applications benefit from grouping routing logic alongside related domain and data components.

```Generic
customer/
    routes/
        CustomerRoutes.kt
        CustomerCreate.kt
        CustomerDetails.kt
    domain/
        Customer.kt
        CustomerService.kt
    data/
        CustomerRepository.kt
```

Then use a single entry point to expose the feature’s routing:

```kotlin
fun Application.configureCustomer() {
    routing {
        customerRoutes()
    }
}
```

Feature-based organization keeps all functionality for a domain area in one place and scales well as your
application grows.

## Using nested routes and URL structure

Nested routes help express common URL prefixes and shared parameters: