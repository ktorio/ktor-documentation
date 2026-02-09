[//]: # (title: Dependency registration)

<show-structure for="chapter" depth="2"/>

Ktor’s dependency injection (DI) container needs to know how to create objects that your application depends on. This
process is called dependency registration.

### Basic dependency registration

Basic dependency registration is done in code using the `dependencies {}` block.

You can register dependencies by providing [lambdas](#lambda-registration), [function references](#function-reference),
[class references](#class-reference), or [constructor references](#constructor-reference):

#### Use a lambda {id="lambda-registration"}

Use a lambda when you want full control over how an instance is created:

```kotlin
dependencies {
    provide<GreetingService> { GreetingServiceImpl() }
}
```
This registers a provider for `GreetingService`. Whenever `GreetingService` is requested, the lambda is executed to
create an instance.

#### Use a constructor reference {id="constructor-reference"}

If a class can be created using its constructor and all constructor parameters are already registered in the DI
container, you can use a constructor reference.

```kotlin
dependencies {
    provide<GreetingService>(::GreetingServiceImpl)
}
```
This tells your application to use the constructor of `GreetingServiceImpl`, and let DI resolve its parameters.

#### Use a class reference {id="class-reference"}

You can register a concrete class without binding it to an interface:

```kotlin
dependencies {
    provide(BankServiceImpl::class)
}
```
In this case, the dependency is resolved by its `BankServiceImpl` type.
This is useful when the implementation type is injected directly and no abstraction is required.

#### Use a function reference {id="function-reference"}

You can register a function that creates and returns an instance:

```kotlin
dependencies {
    provide(::createBankTeller)
}
```

The DI container resolves the function parameters and uses the return value as the dependency instance.

#### Use a factory lambda {id="factory-lambda-registration"}

You can register a function itself as a dependency:

```kotlin
dependencies {
    provide<() -> GreetingService> {
        { GreetingServiceImpl() }
    }
}
```

This registers a function that can be injected and called manually to create new instances.

### Configuration-based dependency registration

You can configure dependencies declaratively using classpath references in your configuration file. You can list a
function that returns an object, or a class with a resolvable constructor.

List the dependencies under the `ktor.application.dependencies` group in your configuration file:

<tabs>
<tab title="application.yaml">

```yaml
```
{src="snippets/server-di/src/main/resources/application.yaml" include-lines="1,4-7"}

</tab>
</tabs>

Ktor resolves function and constructor parameters automatically using the DI container.
