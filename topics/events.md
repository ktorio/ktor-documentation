[//]: # (title: Application monitoring)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="events"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Ktor provides the ability to monitor your server application by using events.
You can handle predefined events related to an application's lifecycle (application starting, stopping, etc.), or you
can use custom events to handle specific cases. You can also handle events for custom plugins using
the [MonitoringEvent](custom_plugins.md#handle-app-events) hook.

## Event definition {id="event-definition"}

Each event is represented by
the [EventDefinition](https://api.ktor.io/ktor-shared/ktor-events/io.ktor.events/-event-definition/index.html) class
instance. This class has a `T` type parameter specifying the type of value passed to the event. This value can be
accessed in the [event handler](#handle-events-application) as a lambda argument. For example, most of
the [predefined events](#predefined-events) accept `Application` as a parameter allowing you to access application
properties inside the event handler.

For a [custom event](#custom-events), you can pass a type parameter required for this event.
The code snippet below shows how to create a custom `NotFoundEvent` that accepts the `ApplicationCall` instance.

```kotlin
```

{src="snippets/events/src/main/kotlin/com/example/plugins/ApplicationMonitoringPlugin.kt" include-lines="25"}

The [](#custom-events) section shows how to raise this event in a custom plugin when a server returns
the `404 Not Found` status code for a resource.

### Predefined application events {id="predefined-events"}

Ktor provides the following predefined events related to an application's lifecycle:

- [ApplicationStarting](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-starting.html)
- [ApplicationStarted](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-started.html)
- [ServerReady](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-server-ready.html)
- [ApplicationStopPreparing](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-stop-preparing.html)
- [ApplicationStopping](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-stopping.html)
- [ApplicationStopped](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-stopped.html)

For example, you can subscribe to the `ApplicationStopped` event to release application resources.

## Handle events in an application {id="handle-events-application"}

To handle events for the specified `Application` instance, use the `monitor` property.
This property provides access to
the [Events](https://api.ktor.io/ktor-shared/ktor-events/io.ktor.events/-events/index.html) instance that exposes the
following functions allowing you to handle application events:

- `subscribe`: subscribes to an event specified by [EventDefinition](#event-definition).
- `unsubscribe`: unsubscribes from an event specified by [EventDefinition](#event-definition).
- `raise`: raises an event specified by [EventDefinition](#event-definition) with the specified value.
  > The [](#custom-events) section shows how to raise custom events.

The `subscribe` / `unsubscribe` functions accept the `EventDefinition` instance with the `T` value as a lambda argument.
The example below shows how to subscribe to the `ApplicationStarted` event and [log](logging.md) a message in the event
handler:

```kotlin
```

{src="snippets/events/src/main/kotlin/com/example/Application.kt" include-lines="11,13-15,30"}

In this example, you can see how to handle the `ApplicationStopped` event:

```kotlin
```

{src="snippets/events/src/main/kotlin/com/example/Application.kt" include-lines="11,16-21,30"}

For the full example, see [events](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/events).

## Handle events in a custom plugin {id="handle-events-plugin"}

You can handle events in [custom plugins](custom_plugins.md#handle-app-events) using the `MonitoringEvent` hook.
The example below shows how to create the `ApplicationMonitoringPlugin` plugin and handle the `ApplicationStarted`
and `ApplicationStopped` events:

```kotlin
```

{src="snippets/events/src/main/kotlin/com/example/plugins/ApplicationMonitoringPlugin.kt" include-lines="3-17,23"}

You can find the full example
here: [events](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/events).

## Custom events {id="custom-events"}

In this section, we'll take a look at how to create a custom event raised when a server returns the `404 Not Found`
status code for a resource.

1. First, you need to create the [event definition](#event-definition).
   The code snippet below shows how to create a custom `NotFoundEvent` event that accepts `ApplicationCall` as a
   parameter.

   ```kotlin
   ```
   {src="snippets/events/src/main/kotlin/com/example/plugins/ApplicationMonitoringPlugin.kt" include-lines="25"}
2. To raise the event, call the `Events.raise` function. The sample below shows how to handle
   the `ResponseSent` [hook](custom_plugins.md#other) to raise the newly created event if the status code for a call
   is `404`.

   ```kotlin
   ```
   {src="snippets/events/src/main/kotlin/com/example/plugins/ApplicationMonitoringPlugin.kt" include-lines="3-8,18-23"}
3. To handle the created event in the Application, [install](Plugins.md#install) the plugin:

   ```kotlin
   ```
   {src="snippets/events/src/main/kotlin/com/example/Application.kt" include-lines="11-12,30"}

4. Then, subscribe to the event using `Events.subscribe`:

   ```kotlin
   ```
   {src="snippets/events/src/main/kotlin/com/example/Application.kt" include-lines="11-12,22-24,30"}

For the full exmaple, see [events](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/events).
