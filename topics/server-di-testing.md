[//]: # (title: Testing with dependency injection)

<show-structure for="chapter" depth="2"/>

The dependency injection (DI) plugin provides tooling to simplify testing.

You can override dependencies before loading your application modules:

```kotlin
fun test() = testApplication {
  application {
    dependencies.provide<MyService> {
      MockService()
    }
    loadServices()
  }
}
```

### Loading configuration in tests

Use `configure()` to load configuration files easily in your tests:

```kotlin
fun test() = testApplication {
  // Load properties from the default config file path
  configure()
  // Load multiple files with overrides
  configure("root-config.yaml", "test-overrides.yaml")
}
```

Conflicting declarations are ignored by the test engine to let you override freely.
