// TODO: this file should be removed when API is in main Ktor repo

package io.ktor.application.newapi

import io.ktor.application.*
import io.ktor.application.newapi.KtorFeature.Companion.makeFeature
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import java.nio.channels.*
import java.nio.channels.ByteChannel
import kotlin.random.*

public interface FeatureContext {
    public fun onCall(callback: suspend CallExecution.(ApplicationCall) -> Unit): Unit
    public fun onReceive(callback: suspend ReceiveExecution.(ApplicationCall) -> Unit): Unit
    public fun onRespond(callback: suspend SendExecution.(ApplicationCall) -> Unit): Unit
}

public data class Interception<T : Any>(
    val phase: PipelinePhase,
    val action: (Pipeline<T, ApplicationCall>) -> Unit
)

public typealias CallInterception = Interception<Unit>
public typealias ReceiveInterception = Interception<ApplicationReceiveRequest>
public typealias SendInterception = Interception<Any>

public interface InterceptionsHolder {
    public val name: String get() = this.javaClass.simpleName
    public val callInterceptions: MutableList<CallInterception>
    public val receiveInterceptions: MutableList<ReceiveInterception>
    public val sendInterceptions: MutableList<SendInterception>
    public fun newPhase(): PipelinePhase = PipelinePhase("${name}Phase${Random.nextInt()}")
}

public inline class Execution<ContextT : Any>(private val ctx: PipelineContext<ContextT, ApplicationCall>) {
    public suspend fun proceed(): ContextT = ctx.proceed()
    public suspend fun proceedWith(subectT: ContextT): ContextT = ctx.proceedWith(subectT)
    public fun finish(): Unit = ctx.finish()
    public val context: ContextT get() = ctx.subject
    public val call: ApplicationCall get() = ctx.call
}

public typealias CallExecution = Execution<Unit>
public typealias ReceiveExecution = Execution<ApplicationReceiveRequest>
public typealias SendExecution = Execution<Any>

public abstract class KtorFeature<Configuration : Any>(
    public override val name: String
) : ApplicationFeature<Application, Configuration, KtorFeature<Configuration>>, FeatureContext, InterceptionsHolder {

    protected var configurationValue: Configuration? = null

    public val feature: Configuration get() = configurationValue!!

    override val key: AttributeKey<KtorFeature<Configuration>> = AttributeKey(name)

    override val callInterceptions: MutableList<CallInterception> = mutableListOf()
    override val receiveInterceptions: MutableList<ReceiveInterception> = mutableListOf()
    override val sendInterceptions: MutableList<SendInterception> = mutableListOf()

    private fun <T : Any> onDefaultPhase(
        interceptions: MutableList<Interception<T>>,
        phase: PipelinePhase,
        callback: suspend Execution<T>.(ApplicationCall) -> Unit
    ) {
        interceptions.add(Interception(
            phase,
            action = { pipeline ->
                pipeline.intercept(phase) { Execution(this).callback(call) }
            }
        ))
    }


    public class CallContext(private val feature: KtorFeature<*>) {
        public fun monitoring(callback: suspend CallExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.callInterceptions, ApplicationCallPipeline.Monitoring, callback)

        public fun fallback(callback: suspend CallExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.callInterceptions, ApplicationCallPipeline.Fallback, callback)

        public fun onCall(callback: suspend CallExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.callInterceptions, ApplicationCallPipeline.Features, callback)
    }

    // AppilicationCallPipeline interceptor
    public fun extendCallHandling(build: CallContext.() -> Unit): Unit =
        CallContext(this).build()

    // default
    public override fun onCall(callback: suspend CallExecution.(ApplicationCall) -> Unit): Unit =
        extendCallHandling {
            onCall {
                callback(it)
            }
        }


    public class ReceiveContext(private val feature: KtorFeature<*>) {
        public fun beforeReceive(callback: suspend ReceiveExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.receiveInterceptions, ApplicationReceivePipeline.Before, callback)

        public fun onReceive(callback: suspend ReceiveExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.receiveInterceptions, ApplicationReceivePipeline.Transform, callback)
    }

    // ApplicationReceivePipeline interceptor
    public fun extendReceiveHandling(build: ReceiveContext.() -> Unit): Unit = ReceiveContext(this).build()

    // default
    public override fun onReceive(callback: suspend ReceiveExecution.(ApplicationCall) -> Unit): Unit =
        extendReceiveHandling {
            onReceive {
                callback(it)
            }
        }

    public class SendContext(private val feature: KtorFeature<*>) {
        public fun beforeSend(callback: suspend SendExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.sendInterceptions, ApplicationSendPipeline.Before, callback)

        public fun afterSend(callback: suspend SendExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.sendInterceptions, ApplicationSendPipeline.After, callback)

        public fun onSend(callback: suspend SendExecution.(ApplicationCall) -> Unit): Unit =
            feature.onDefaultPhase(feature.sendInterceptions, ApplicationSendPipeline.Transform, callback)
    }

    // ApplicationSendPipeline interceptor
    public fun extendResponseHandling(build: SendContext.() -> Unit): Unit =
        SendContext(this).build()

    // default
    public override fun onRespond(callback: suspend SendExecution.(ApplicationCall) -> Unit): Unit =
        extendResponseHandling {
            onSend {
                callback(it)
            }
        }

    public abstract class RelativeFeatureContext(private val feature: InterceptionsHolder) : FeatureContext {
        protected fun <T : Any> sortedPhases(
            interceptions: List<Interception<T>>,
            pipeline: Pipeline<*, ApplicationCall>
        ): List<PipelinePhase> =
            interceptions
                .map { it.phase }
                .sortedBy {
                    if (!pipeline.items.contains(it)) {
                        throw FeatureNotInstalledException(feature.name)
                    }

                    pipeline.items.indexOf(it)
                }

        public abstract fun selectPhase(phases: List<PipelinePhase>): PipelinePhase?

        public abstract fun insertPhase(
            pipeline: Pipeline<*, ApplicationCall>,
            relativePhase: PipelinePhase,
            newPhase: PipelinePhase
        )

        private fun <T : Any> onDefaultPhase(
            interceptions: MutableList<Interception<T>>,
            callback: suspend Execution<T>.(ApplicationCall) -> Unit
        ) {
            val currentPhase = feature.newPhase()

            interceptions.add(
                Interception(
                    currentPhase,
                    action = { pipeline ->
                        val phases = sortedPhases(feature.callInterceptions, pipeline)
                        selectPhase(phases)?.let { lastDependentPhase ->
                            insertPhase(pipeline, lastDependentPhase, currentPhase)
                        }
                        pipeline.intercept(currentPhase) {
                            Execution(this).callback(call)
                        }
                    }
                )
            )
        }

        override fun onCall(callback: suspend CallExecution.(ApplicationCall) -> Unit): Unit =
            onDefaultPhase(feature.callInterceptions, callback)

        override fun onReceive(callback: suspend ReceiveExecution.(ApplicationCall) -> Unit): Unit =
            onDefaultPhase(feature.receiveInterceptions, callback)

        override fun onRespond(callback: suspend SendExecution.(ApplicationCall) -> Unit): Unit =
            onDefaultPhase(feature.sendInterceptions, callback)
    }

    public class AfterFeatureContext(feature: InterceptionsHolder) : RelativeFeatureContext(feature) {
        override fun selectPhase(phases: List<PipelinePhase>): PipelinePhase? = phases.lastOrNull()

        override fun insertPhase(
            pipeline: Pipeline<*, ApplicationCall>,
            relativePhase: PipelinePhase,
            newPhase: PipelinePhase
        ) {
            pipeline.insertPhaseAfter(relativePhase, newPhase)
        }

    }

    public class BeforeFeatureContext(feature: InterceptionsHolder) : RelativeFeatureContext(feature) {
        override fun selectPhase(phases: List<PipelinePhase>): PipelinePhase? = phases.firstOrNull()

        override fun insertPhase(
            pipeline: Pipeline<*, ApplicationCall>,
            relativePhase: PipelinePhase,
            newPhase: PipelinePhase
        ) {
            pipeline.insertPhaseBefore(relativePhase, newPhase)
        }

    }

    public fun afterFeature(feature: InterceptionsHolder, build: AfterFeatureContext.() -> Unit): Unit =
        AfterFeatureContext(this).build()


    public fun beforeFeature(feature: InterceptionsHolder, build: BeforeFeatureContext.() -> Unit): Unit =
        BeforeFeatureContext(this).build()

    public companion object {
        public fun <Configuration : Any> makeFeature(
            name: String,
            createConfiguration: (Application) -> Configuration,
            body: KtorFeature<Configuration>.() -> Unit
        ): KtorFeature<Configuration> = object : KtorFeature<Configuration>(name) {

            override fun install(
                pipeline: Application,
                configure: Configuration.() -> Unit
            ): KtorFeature<Configuration> {
                configurationValue = createConfiguration(pipeline)
                configurationValue!!.configure()

                this.apply(body)

                callInterceptions.forEach {
                    it.action(pipeline)
                }

                receiveInterceptions.forEach {
                    it.action(pipeline.receivePipeline)
                }

                sendInterceptions.forEach {
                    it.action(pipeline.sendPipeline)
                }

                return this
            }
        }
    }
}