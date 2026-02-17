[//]: # (title: Dependency injection)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>
<var name="artifact_name" value="ktor-server-di" />

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-di</code>
</p>
<var name="example_name" value="server-di"/>
<include from="lib.topic" element-id="download_example" />
</tldr>

[Dependency injection (DI)](https://en.wikipedia.org/wiki/Dependency_injection) is a design pattern that helps you
supply components with the dependencies they require. Instead of creating concrete implementations directly, modules
depend on abstractions, and a DI container is responsible for constructing and providing the appropriate instances at
runtime. This separation reduces coupling, improves testability, and makes it easier to replace or reconfigure
implementations without modifying existing code.

Ktor provides a built‑in DI plugin that lets you register services and configuration objects once and access them
throughout your application. You can [inject these dependencies into modules](server-di-dependency-resolution.md#inject-into-modules),
plugins, routes, and other Ktor components in a consistent, type‑safe way. The plugin integrates with the Ktor
application lifecycle and supports scoping, structured configuration, and [automatic resource management](server-di-resource-lifecycle-management.md),
making it easier to organize and maintain application‑level services.

## Add dependencies

To use DI, include the `%artifact_name%` artifact in your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## How dependency injection works in Ktor

In Ktor, dependency injection is a single, integrated process that consists of two closely related steps:

* [Registering dependencies](server-di-dependency-registration.md) — declaring how instances are created.
* [Resolving dependencies](server-di-dependency-resolution.md) — accessing and injecting those instances at runtime.

These steps are handled by a single DI container.

To begin using dependency injection in your application, start with [registering dependencies](server-di-dependency-registration.md).
Once dependencies are declared, continue with [resolving dependencies](server-di-dependency-resolution.md).

## Supported features

The DI plugin supports a range of features intended to cover common application needs:

* [Type-safe dependency resolution](server-di-dependency-resolution.md).
* [Optional and nullable dependencies](server-di-dependency-resolution.md#optional-dependencies).
* [Covariant generic resolution](server-di-dependency-resolution.md#covariant-generics).
* [Asynchronous dependency resolution](server-di-dependency-resolution.md#async-dependency-resolution).
* [Automatic and custom resource lifecycle management](server-di-resource-lifecycle-management.md).

## Configuration and lifecycle behavior

The behavior of the DI container can be customized using configuration options. These options control how dependency
keys are matched, how conflicts are handled, and how resolution behaves in advanced scenarios.

For configuration details, see [](server-di-configuration.md).

For resource cleanup and shutdown behavior, see [](server-di-resource-lifecycle-management.md).

## Testing with dependency injection

The DI plugin integrates with Ktor’s testing utilities and supports overriding dependencies, loading configuration, and
controlling conflict behavior in test environments.

For more information and examples, see [](server-di-testing.md).
