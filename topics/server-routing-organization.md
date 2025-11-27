[//]: # (title: Routing organization)

<link-summary>
Learn how to organize and structure your routing logic as your Ktor application grows.
</link-summary>

One of Ktor's strong points is its flexibility and that it does not enforce a single routing organization strategy.
Instead, you can organize routes in a way that best fits the size and complexity of your application, and many projects
combine the patterns described below.

This page shows common patterns for organizing routing code as your project grows.

## Group by file {id="group_by_file"}

One way to organize routing is to place related routes in separate files. This keeps route definitions small and
readable.

For example, if your application is managing customers and orders, you could split the routing logic between 
<path>CustomerRoutes.kt</path> and <path>OrderRoutes.kt</path> files:

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

Then, register each group in your routing block:

```kotlin
routing {
    customerRoutes()
    orderRoutes()
}
```

## Group by feature or domain {id="group_by_feature"}

As your application grows, grouping by domain or feature becomes more scalable.
Each feature gets its own folder containing routes and related logic:

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

Your routing files stay minimal, while business logic is placed in dedicated components.

```kotlin
fun Route.customerRoutes(
    service: CustomerService
) {
    route("/customer") {
        get("/{id}") { call.respond(service.get(call.parameters["id"]!!)) }
        post { call.respond(service.create(call.receive<CustomerDto>())) }
    }
}
```

Feature-based organization keeps all functionality for a domain area in one place and scales well as your
application grows.

## Group routes by path and nest resources {id="group_by_path"}

You can organize routes by grouping all handlers for the same path and nesting related resources.
Each route call adds a path segment and lets you define multiple verbs or child paths:

```kotlin
routing {
    route("/customer") {
        get { /* list customers */ }
        post { /* create customer */ }

        route("/{id}") {
            get { /* get customer */ }
            put { /* update customer */ }
            delete { /* delete customer */ }
        }
    }
}
```

Grouping by path makes the structure easier to follow when defining resource-like APIs.

## Group by package (folders) {id="group_by_folder"}

If file size becomes an issue, you can distribute routing logic into multiple files inside a dedicated package:

```Generic
customer/
  ListCustomers.kt
  CreateCustomer.kt
  GetCustomer.kt
  UpdateCustomer.kt
  CustomerRoutes.kt 
```

Each file contains a small part of the routing logic, while the folder represents the domain.

<tabs>
<tab title="CreateCustomer.kt">

```kotlin
fun Route.createCustomerRoute(service: CustomerService) {
    post("/customer") {
        val body = call.receive<CustomerDto>()
        call.respond(service.create(body))
    }
}
```
</tab>
<tab title="CustomerRoutes.kt">

```kotlin
fun Route.customerRoutes(service: CustomerService) {
    listCustomersRoute(service)
    getCustomerRoute(service)
    createCustomerRoute(service)
    updateCustomerRoute(service)
}
```
</tab>
</tabs>

This approach is useful for large APIs with many endpoints.