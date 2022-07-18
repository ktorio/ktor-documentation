[//]: # (title: Pipelines)

<include from="lib.topic" element-id="outdated_warning"/>

## Description

The pipeline is a structure containing a sequence of functions (blocks/lambdas) that are called one after another, distributed in phases topologically ordered, with the ability to mutate the sequence and to call the remaining functions in the pipeline and then return to current block.

All the functions are suspending blocks/lambdas, thus the whole pipeline is asynchronous.

Since pipelines contain blocks of code, they can be nested, effectively creating sub-pipelines.

Pipelines are used in Ktor as an extension mechanism to plug functionality in at the right place. For example, a Ktor application defines five main phases: Setup, Monitoring, Plugins, Call and Fallback. The Routing plugin defines its own nested pipeline inside the application's call phase.

## API

The simplified API of pipelines (without some generics, only public members and no bodies) looks like this:

```kotlin
class PipelinePhase(val name: String)
class Pipeline <TSubject : Any, TContext : Any> {
    constructor(vararg phases: PipelinePhase)

    val attributes: Attributes
    
    fun addPhase(phase: PipelinePhase)
    fun insertPhaseAfter(reference: PipelinePhase, phase: PipelinePhase)
    fun insertPhaseBefore(reference: PipelinePhase, phase: PipelinePhase)
    
    fun intercept(phase: PipelinePhase, block: suspend PipelineContext.(TSubject) -> Unit)
    
    fun merge(from: Pipeline)

    suspend fun execute(context: TContext, subject: TSubject): TSubject
}
```  

## Phases

Phases are groups of interceptors that can be ordered topologically, defining relationships between them.

You can define your own pipeline phases like this:

```kotlin
val myPipelinePhase = PipelinePhase("MyPipelinePhase")
```

You can register phases when constructing the pipeline: `Pipeline(phase1, phase2, phase3...)`

You can also register your phase later in a pipeline by calling the `Pipeline.addPhase` method, or register it by defining a relation to another phase, adjusting the order of the phases by using the
`Pipeline.insertPhaseAfter` and `Pipeline.insertPhaseBefore` methods, thereby defining relations for phases
to be topologically sorted.

For example, if you define two phases and want them to be executed in order, you can:
```kotlin
val phase1 = PipelinePhase("MyPhase1")
val phase2 = PipelinePhase("MyPhase2")
pipeline.insertPhaseAfter(ApplicationCallPipeline.Plugins, phase1)
pipeline.insertPhaseAfter(phase1, phase2)
```

Then you can intercept phases, so your interceptors will be called in that phase in the order they are registered:

```kotlin
pipeline.intercept(phase1) { println("Phase1[A]") }
pipeline.intercept(phase2) { println("Phase2[A]") }
pipeline.intercept(phase2) { println("Phase2[B]") }
pipeline.intercept(phase1) { println("Phase1[B]") }
```

Would print:
```text
Phase1[A]
Phase1[B]
Phase2[A]
Phase2[B]
```

You can execute the pipeline by calling the `execute` method, providing a context and a subject:

```kotlin
pipeline.execute(context, subject)
```

You can omit calling the `addPhase` method when using the `insertPhase*` methods unless you need to register a Phase that would otherwise be included by calling `Pipeline.merge` later.

>For example if you define a Phase inside a node in the Routing plugin, and then, in an inner node, try to insert a phase using that one as reference, you would get an exception similar to `io.ktor.pipeline.InvalidPhaseException: Phase Phase('YourPhase') was not registered for this pipeline`.
>In this case you can just call `addPhase`, so the phase is referenced before merging.
>
{type="note"}

## Interceptors and the PipelineContext

When calling `Pipeline.intercept` you provide a phase where the interception will be appended,
and you also have to provide a function/lambda that receives a `this: PipelineContext`,
so you can handle whatever you need. Inside that context, you have access to a properly typed context (usually the `ApplicationCall`) and an optional `Subject`, so you can pass information to other interceptors.

The context API:

```kotlin
class PipelineContext<TSubject : Any, out TContext : Any>() {
    val context: TContext
    val subject: TSubject
    
    fun finish()
    suspend fun proceedWith(subject: TSubject): TSubject
    suspend fun proceed(): TSubject
}
```

This way, the interceptors can control the flow in the following ways:

* Throwing an exception: The exception propagates back, and the pipeline is canceled.
* Calling the `proceed` or `proceedWith` functions: The interceptor is suspended, while the rest of the pipeline is executed. Once completed the function is resumed, and the `proceed`/`proceedWith` code block is executed.
* Calling the `finish()` function: The pipeline finishes without any exceptions and without executing the rest of the pipeline.
* In other cases: The next function is called, or the pipeline finishes if it was the last function.

The order of the blocks is determined first by the order of phases they are installed into, then by installation order.

Phases are defined when the pipeline is created and can be augmented to add more phases using `pipeline.phases`.

>For a `PipelineContext` that has an `ApplicationCall` as context, there is a convenience extension property `call` as an alias to `context`.
>
{type="note"}

## The Subject

During execution the pipeline context also carries a _subject_: an arbitrarily typed generic object `TSubject` that is being _processed_.

The `subject` is accessible from the context as a property with its own name, and it is propagated between interceptors.
You can change the instance (for example for immutable subjects) using the `PipelineContext.proceedWith(subject)` method.

When using this method, the pipeline will continue with the new subject instance and will return to the caller with the last instance passed by the pipeline, effectively allowing it to process the subject in later interceptions.

>For a pipeline without a subject you can use `Unit`, for example, since the `ApplicationCallPipeline` doesn't require a subject; it uses `Unit`.
>
{type="note"}

## Merging

Pipelines of the same type can be merged. This is done with the `merge` function on a receiving pipeline.

All interceptors from pipeline merging are added to the receiving pipeline according to their phases.

Pipelines are merged when there are different points where interceptors can be installed. One example is the response pipeline that can be intercepted at the application level, call level, or per route. Before we execute a response pipeline, we merge them all.

## Ktor pipelines

### ApplicationCallPipeline
{id="ApplicationCallPipeline"}

Ktor defines a pipeline without a subject, and the `ApplicationCall` as a context
defining five phases (`Setup`, `Monitoring`, `Plugins`, `Call` and `Fallback`) to be executed in this order:

```nomnoml
#direction: right
#.call: fill=#af8
#.fallback: fill=#faa dashed
[<call>Call]
[<fallback>Fallback]

[Setup] then -> [Monitoring]
[Monitoring] then -> [Plugins]
[Plugins] then -> [Call]
[Call] then -> [Fallback]
```

The purpose for intercepting each phase:
* `Setup`: phase used for preparing the call and its attributes for processing (like the [CallId] plugin).
* `Monitoring`: phase used for tracing calls: useful for logging, metrics, error handling and so on (like the [CallLogging] plugin).
* `Plugins`: most plugins should intercept this phase (like the [Authentication] plugin).
* `Call`: plugins and interceptors used to complete the call, like the [Routing] plugin.
* `Fallback`: plugins that process unhandled calls in a normal way and resolve them somehow, like the [StatusPages] plugin.

[CallId]: call-id.md
[CallLogging]: call-logging.md
[Authentication]: authentication.md
[Routing]: Routing_in_Ktor.md
[StatusPages]: status_pages.md

The code looks like this:

```kotlin
open class ApplicationCallPipeline : Pipeline<Unit, ApplicationCall>(Setup, Monitoring, Plugins, Call, Fallback) {
    val receivePipeline = ApplicationReceivePipeline()
    val sendPipeline = ApplicationSendPipeline()

    companion object {
        val Setup = PipelinePhase("Setup")
        val Monitoring = PipelinePhase("Monitoring")
        val Plugins = PipelinePhase("Plugins")
        val Call = PipelinePhase("Call")
        val Fallback = PipelinePhase("Fallback")
    }
}
```

This base pipeline is used by the `Application` and the `Routing` plugins.

### Application

A Ktor `Application` is an `ApplicationCallPipeline`. This is the main pipeline used for web backend
applications handling http requests.

### Routing

The [Routing plugin](Routing_in_Ktor.md) defines a nested pipeline attached to the Call phase in the Application pipeline.
You can get the routing root node pipeline by calling `val routing = application.routing {}`.
Each node in the `Route` tree defines its own pipeline that is later merged per each route.

By merging the tree pipelines, you can define phases and interceptions at some point in the tree, and then they will be executed in the order defined by the phase relationships.

>Since `Route` nodes have their own pipeline and the merge happens later, if you plan to add relationships to some phases defined in other ancestor `Route` nodes, you will have to add them with `Pipeline.addPhase` in the specific `Route` node to avoid the `io.ktor.pipeline.InvalidPhaseException: Phase Phase('YourPhase') was not registered for this pipeline` exception.
>
{type="note"}

### Other

Ktor also defines other pipelines in some other plugins.

## Samples

See [`PipelineTest`](https://github.com/ktorio/ktor/blob/main/ktor-utils/jvm/test/io/ktor/tests/utils/PipelineTest.kt) for examples.