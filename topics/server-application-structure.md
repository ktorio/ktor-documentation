[//]: # (title: Application structure)

<link-summary>
Learn how to organize your Ktor application for maintainability, modularity, and dependency injection.
</link-summary>

<show-structure for="chapter" depth="2"/>

Ktor applications can be organized in several ways depending on project size, domain complexity, and deployment
environment. While Ktor is intentionally unopinionated, there are common patterns and best practices that help keep your
application modular, testable, and easy to extend.

This topic describes common structures used in Ktor projects and provides practical recommendations for choosing and
applying one.

> This page focuses on application-level structure. For more information on structuring routes, see
> [](server-routing-organization.md).
>

## Default project structure

When you generate a Ktor project using [the Ktor project generator](https://start.ktor.io/), the resulting project uses
a single-module structure. This layout is minimal and intended to get you up and running quickly with a working Ktor
application.

```
project/
└─ src/
   ├─ main/
   │  ├─ kotlin/
   │  │  └─ Application.kt   // Application entry point
   │  └─ resources/
   │     └─ application.conf  // Application configuration
   └─ test/
      └─ kotlin/             // Unit and integration tests
├─ build.gradle.kts       // Gradle build file
└─ settings.gradle.kts    // Gradle settings file
```

Although suitable for small applications, this structure does not scale well as the project grows. For larger projects,
it is recommended to organize functionality into logical packages and modules, as described in the following sections.

## Choosing an application structure {id="choosing_structure"}

Selecting the right structure depends on the characteristics of your service:

- Small services often work well with only a few [modules](#modular_architecture) and simple dependency injection.
- Medium-sized applications typically benefit from a consistent [feature-based structure](#feature_modules) that groups related routes, 
services, and data models together. 
- Large or domain-heavy systems can adopt [a domain-driven approach](#ddd), which provides clearer boundaries and
organizes business logic around domain concepts.
- [Microservice architectures](#microservice-oriented-structure) normally use a hybrid style, where each service
represents a domain slice and is internally modular.

It’s worth noting that these structures are not mutually exclusive. You can combine multiple approaches — for example,
using a feature-based organization within a domain-driven architecture, or applying modularity in a microservice-oriented
system.

## Layered structure {id="layered_structure"}

A layered architecture separates your application into distinct responsibilities: configuration, plugins, routes,
business logic, persistence, domain models, and data transfer objects (DTOs). This approach is common in enterprise 
applications and provides a clear starting point for maintainable code.

```
src/main/kotlin/com/example/app/
├─ config/            // Application configuration and environment setup
├─ plugins/           // Ktor plugins (authentication, serialization, monitoring)
├─ controller/        // Routes or API endpoints
├─ service/           // Business logic
├─ repository/        // Data access or persistence
├─ domain/            // Domain models and aggregates
└─ dto/               // Data transfer objects
```

## Modular architecture {id="modular_architecture"}

Ktor encourages modular design by allowing you to define multiple application modules. A module is a function extending
`Application` that configures part of the application:

```kotlin
fun Application.customerModule() {
    //…
}
```

Each module can install plugins, configure routes, register services, or integrate infrastructure components. Modules 
can depend on each other or remain fully independent, which makes this structure flexible for both monoliths and
microservices.

Dependencies are typically injected at module boundaries:

```kotlin
fun Application.customerModule(customerService: CustomerService) {
    routing {
        customerRoutes(customerService)
    }
}
```

### Why modularize?

A modular structure helps you:

- Separate concerns and isolate feature logic
- Enable configuration or plugin installation only where needed
- Improve testability by instantiating modules in isolation
- Support microservice-friendly or plugin-friendly code organization
- Introduce dependency injection at module boundaries

> For a full example of a modular, layered Ktor server application, see the [Ktor Chat](https://github.com/ktorio/ktor-chat)
> sample project. It demonstrates a modular architecture with separate domain, application, and infrastructure layers,
> as well as dependency injection and routing organization.

## Feature-based modules {id="feature_modules"}

Feature-based organization groups code by feature or vertical slice. Each feature becomes a
self-contained module, containing its routes, services, data transfer objects (DTOs) and domain logic.

```
app/
├─ customer/
│  ├─ CustomerRoutes.kt     // Routing for customer endpoints
│  ├─ CustomerService.kt    // Business logic for customer feature
│  └─ CustomerDto.kt        // Data transfer objects for customer feature
└─ order/
   ├─ OrderRoutes.kt        // Routing for order endpoints
   ├─ OrderService.kt       // Business logic for order feature
   └─ OrderDto.kt           // Data transfer objects for order feature
```

This structure scales well in medium-to-large monoliths or when splitting individual features into microservices later.
Each feature can be migrated or versioned independently. A typical feature module may look like this:

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

In the above example, the module has no knowledge of how `CustomerService` is created — it only receives it, which keeps
dependencies explicit.


## Domain-driven design (DDD) approach {id="ddd"}

A domain-driven structure organizes your application around the core business capabilities it represents. For large
projects with complex business rules, it is helpful to separate domain logic from transport, persistence, and
infrastructure concerns:

```
domain/
├─ customer/
│  ├─ Customer.kt           // Domain entity
│  ├─ CustomerService.kt    // Domain service
│  ├─ CustomerRepository.kt // Domain repository interface
│  └─ CustomerRoutes.kt     // Feature routes exposing domain functionality
├─ order/
│  ├─ Order.kt
│  ├─ OrderService.kt
│  └─ OrderRepository.kt
infrastructure/
├─ persistence/
│  ├─ ExposedCustomerRepository.kt // Concrete persistence implementation
│  └─ ExposedOrderRepository.kt
├─ messaging/                    // Event messaging infrastructure
└─ config/                       // Application configuration for infrastructure
events/
├─ DomainEvents.kt               // Domain event definitions
└─ EventPublisher.kt             // Event publishing utilities
```
### Domain layer

The domain layer remains independent of Ktor. It defines the business rules through the following elements:

- _Entities_ represent identifiable domain objects:
```kotlin
data class Customer(
    val id: CustomerId,
    val contacts: List<Contact>
)
```
- _Value objects_ express immutable concepts such as identifiers or validated fields:
```kotlin
@JvmInline
value class CustomerId(val value: Long)
```
- _Aggregates_ group related entities under a single consistency boundary:
```kotlin
class CustomerAggregate(private val customer: Customer) {

    fun addContact(contact: Contact): Customer =
        customer.copy(contacts = customer.contacts + contact)
}
```

- _Repositories_ abstract persistence and expose operations for retrieving or saving aggregates. Their implementations
live in the infrastructure layer, but the interfaces belong to the domain.
```kotlin
interface CustomerRepository {
    suspend fun find(id: CustomerId): Customer?
    suspend fun save(customer: Customer)
}
```
- _Domain services_ coordinate business logic that spans multiple aggregates or does not naturally belong to a single
entity.
```kotlin
class CustomerService(
    private val repository: CustomerRepository,
    private val events: EventPublisher
) {
    suspend fun addContact(id: CustomerId, contact: Contact): Customer? {
        val customer = repository.find(id) ?: return null
        val updated = CustomerAggregate(customer).addContact(contact)
        repository.save(updated)
        events.publish(CustomerContactAdded(id, contact))
        return updated
    }
}
```
- _Domain events_ represent meaningful business changes. They allow other parts of the system to react to these events
without directly coupling to the service that produced them.
```kotlin
interface DomainEvent

data class CustomerContactAdded(
    val id: CustomerId,
    val contact: Contact
) : DomainEvent
```
These elements together support a rich domain model while keeping infrastructure details separate.

### Application and routing layer

You expose each domain through its own route file or module function, injecting services that manage both logic and
state:

```kotlin
// domain/customer/CustomerRoutes.kt
fun Application.customerRoutes(service: CustomerService) {
    route("/customers") {
        post("/{id}/contacts") {
            val id = call.parameters["id"]!!.toLong()
            val contact = call.receive<Contact>()
            val updated = service.addContact(CustomerId(id), contact)
            call.respond(updated ?: HttpStatusCode.NotFound)
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val customer = service.findById(CustomerId(id))
            call.respond(customer ?: HttpStatusCode.NotFound)
        }
    }
}
```

```kotlin
// Application.kt
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    val customerRepository: CustomerRepository = ExposedCustomerRepository()
    val eventPublisher: EventPublisher = EventPublisherImpl()

    val customerService = CustomerService(customerRepository, eventPublisher)

    routing {
        customerRoutes(customerService)
    }
}
```

> For a complete code example of a domain-driven application, see the [Ktor DDD example](https://github.com/antonarhipov/ktor-ddd-example/tree/main).

## Microservice-oriented structure

Ktor applications can be organized as microservices, where each service is a self-contained module that can be deployed
independently.

Microservice repositories often use a hybrid of modular architecture, DDD for domain isolation and Gradle multi-module
builds for infrastructure isolation.

```
service-customer/
├─ domain/        // Domain models and aggregates
├─ repository/    // Persistence layer for customer service
├─ service/       // Business logic
├─ dto/           // Data transfer objects
├─ controller/    // Routes or API endpoints
├─ plugins/       // Ktor plugin installation for this service
└─ Application.kt // Entry point for the service

service-order/
├─ domain/        // Domain models and aggregates
├─ repository/    // Persistence layer for order service
├─ service/       // Business logic
├─ dto/           // Data transfer objects
├─ controller/    // Routes or API endpoints
├─ plugins/       // Ktor plugin installation for this service
└─ Application.kt // Entry point for the service
```

In this structure, each service owns an isolated domain slice and remains modular internally, integrating with service
discovery, metrics, and external configuration.