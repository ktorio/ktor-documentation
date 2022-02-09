[//]: # (title: Application monitoring)

<include src="lib.xml" include-id="outdated_warning"/>

On the server-side, in addition to handling requests, Ktor exposes a mechanism to produce and consume global events.

For example, when the application is starting, has started, or has stopped, an event is generated and raised.
You can subscribe to or unsubscribe from these events and trigger code execution.
The `monitor: ApplicationEvents` instance, associated with the application environment, acts as the event dispatcher.

The `ApplicationEvents` dispatches typed `EventDefinition<T>` along with an object `T`.

You can get the monitor along with the application instance by executing `application.environment.monitor`.

## `ApplicationEvents` API

The simplified API for the `monitor: ApplicationEvents` looks like this:

```kotlin
class ApplicationEvents {
    fun <T> subscribe(definition: EventDefinition<T>, handler: EventHandler<T>): DisposableHandle
    fun <T> unsubscribe(definition: EventDefinition<T>, handler: EventHandler<T>)
    fun <T> raise(definition: EventDefinition<T>, value: T)
}

class EventDefinition<T>

typealias EventHandler<T> = (T) -> Unit

interface DisposableHandle {
    fun dispose()
}
```

## Predefined EventDefinitions

Ktor provides some predefined events that are dispatched by the engine:

```kotlin
val ApplicationStarting: EventDefinition<Application>
val ApplicationStarted: EventDefinition<Application>
val ApplicationStopPreparing: EventDefinition<ApplicationEnvironment>
val ApplicationStopping: EventDefinition<Application>
val ApplicationStopped: EventDefinition<Application>
```

## Subscribing to events and raising them

You can subscribe to events by calling the `subscribe` method from the monitor.
The subscribe method returns a `DisposableHandle` that you can call to cancel the subscription.
Additionally, you can call the `unsubscribe` method with the same method handle to cancel the subscription.

Using the disposable:

```kotlin
val disposable = application.environment.monitor.subscribe(ApplicationStarting) { application: Application ->
    // Handle the event using the application as subject
}
disposable.dispose() // Cancels the subscription
```

Using a lambda stored in a property:

```kotlin
val starting: (Application) -> Unit = { log("Application starting: $it") }

application.environment.monitor.subscribe(ApplicationStarting, starting) // subscribe
application.environment.monitor.unsubscribe(ApplicationStarting, starting) // unsubscribe
```

Using a method reference:

```kotlin
fun starting(application: Application) { log("Application starting: $it") }

application.environment.monitor.subscribe(ApplicationStarting, ::starting) // subscribe
application.environment.monitor.unsubscribe(ApplicationStarting, ::starting) // unsubscribe
```

If you want to create custom events and dispatch or raise them:

```kotlin
class MySubject
val MyEventDefinition = EventDefinition<MySubject>()
monitor.raise(MyEventDefinition, MySubject())
```

## Examples

You can check the [CallLogging](https://github.com/ktorio/ktor/blob/33519fd01e6a57467e1b12b1297af84d25ace814/ktor-server/ktor-server-plugins/ktor-server-call-logging/jvm/src/io/ktor/server/plugins/callloging/CallLogging.kt) plugin source code that includes code subscribing to events from the application.