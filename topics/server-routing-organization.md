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

Each file groups route handlers that belong to the same domain area. Then, you can register each group in your
routing block:

```kotlin
routing {
    customerRoutes()
    orderRoutes()
}
```

This approach works well for small or medium-sized projects where each domain area only contains a few routes.

## Group by package (folders) {id="group_by_folder"}

As a file grows, it can become harder to navigate. To keep routing logic small and focused, you can distribute routing
logic into multiple files inside a dedicated package:

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

This structure works well with Ktor’s modular design and is ideal for large applications, such as APIs with many
endpoints. Each file remains minimal, domain packages stay cohesive, and dependencies (such as services) can be injected
per route module.

## Group routes by path and nest resources {id="group_by_path"}

You can organize routes by grouping all handlers for the same path and nesting related resources.
Nested routing is useful when an endpoint includes multiple operations on the same resource:

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

Grouping by path keeps related endpoints visually close and makes the routing structure easier to understand from an
HTTP API standpoint.

## Group by feature or domain {id="group_by_feature"}

As your application grows, grouping by domain or feature becomes more scalable.
Each feature gets its own package containing routes and related logic such as DTOs, services, and business rules:

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

Each file contains a small, focused piece of routing logic, and the package acts as a self-contained domain module.

For example, the `CustomerRoutes` file from the above example structure may contain the following route definitions:

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

Feature-based organization keeps all functionality for a domain area in one place and makes it easy to inject 
dependencies such as services or repositories into each domain module. It also scales well as your application grows,
including multi-module Ktor applications.