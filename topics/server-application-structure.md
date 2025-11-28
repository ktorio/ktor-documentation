[//]: # (title: Application structure)

<link-summary>
Learn how to organize your Ktor application for maintainability, modularity, and dependency injection.
</link-summary>

Ktor is highly flexible and does not enforce a single application structure. However, as your application grows, having
a clear structure for modules, features, and services becomes crucial for maintainability and testability.

> This page focuses on application-level structure, including modularity, dependency injection, and domain-driven
> organization. For more information on routing structure, see [](server-routing-organization.md).
>

## Modular architecture {id="modular_architecture"}

Ktor supports building modular applications by creating separate modules for independent features.

A module is a function extending `Application` that installs plugins and configures routes, services, and other
components. Modules can depend on each other or be completely independent.

### Feature-based modules {id="feature_modules"}

A common and recommended approach is to organize your application by feature or domain, where each feature is a
self-contained module.

```generic
customer/
    CustomerRoutes.kt
    CustomerService.kt
order/
    OrderRoutes.kt
    OrderService.kt
```

Each feature gets its own package containing routes and related logic such as DTOs, services, and business rules

Feature-based organization keeps all functionality for a domain area in one place and makes it easy to inject
dependencies such as services or repositories into each module.

### Module definition {id="module_definition"}

```kotlin
fun Application.customerModule(service: CustomerService) {
    routing {
        route("/customer") {
            get("/{id}") { call.respond(service.get(call.parameters["id"]!!)) }
            post {
                val dto = call.receive<CustomerDto>()
                call.respond(service.create(dto))
            }
        }
    }
}
```
## Dependency injection {id="dependency_injection"}

Modules often require services, repositories, or configuration. Injecting dependencies rather than creating them inside
a module improves testability and flexibility.

## Domain-Driven Design (DDD) approach {id="ddd"}

For applications with complex business logic, domain-driven design can help structure modules by domain concepts:

```Generic
domain/
    customer/
        Customer.kt
        CustomerService.kt
        CustomerRepository.kt
    order/
        Order.kt
        OrderService.kt
        OrderRepository.kt
```