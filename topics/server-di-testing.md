[//]: # (title: Testing with dependency injection)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-di</code>
</p>
<var name="example_name" value="server-di"/>
<include from="lib.topic" element-id="download_example" />
</tldr>

The [dependency injection (DI) plugin](server-dependency-injection.md) provides tooling to simplify testing.

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
