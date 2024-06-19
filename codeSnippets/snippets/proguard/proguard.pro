# Proguard Kotlin Example https://github.com/Guardsquare/proguard/blob/master/examples/application-kotlin/proguard.pro

-keepattributes *Annotation*

-keep class kotlin.Metadata { *; }

# Kotlin

-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.text.RegexOption { *; }

# Kotlinx Coroutines https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# These classes are only required by kotlinx.coroutines.debug.AgentPremain, which is only loaded when
# kotlinx-coroutines-core is used as a Java agent, so these are not needed in contexts where ProGuard is used.
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal

# Only used in `kotlinx.coroutines.internal.ExceptionsConstructor`.
# The case when it is not available is hidden in a `try`-`catch`, as well as a check for Android.
-dontwarn java.lang.ClassValue

# An annotation used for build tooling, won't be directly accessed.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Kotlinx Serialization https://github.com/Kotlin/kotlinx.serialization/blob/master/rules/common.pro

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences

# Ktor Server

-keep class io.ktor.server.netty.EngineMain { *; }
-keep class io.ktor.server.config.HoconConfigLoader { *; }
-keep class io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider { *; }

# Logback (Custom rules, see https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-logback-android.pro)
# to ignore warnings coming from slf4j and logback

-keep class ch.qos.** { *; }
-dontwarn ch.qos.**

-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

-dontwarn ch.qos.logback.core.net.*

# Joda-Time Library
-keep class org.joda.time.** { *; }
-dontwarn org.joda.time.**

# Google Guava (https://github.com/google/guava/wiki/UsingProGuardWithGuava)

-dontwarn javax.lang.model.element.Modifier

# Note: We intentionally don't add the flags we'd need to make Enums work.
# That's because the Proguard configuration required to make it work on
# optimized code would preclude lots of optimization, like converting enums
# into ints.

# Throwables uses internal APIs for lazy stack trace resolution
-dontnote sun.misc.SharedSecrets
-keep class sun.misc.SharedSecrets {
  *** getJavaLangAccess(...);
}
-dontnote sun.misc.JavaLangAccess
-keep class sun.misc.JavaLangAccess {
  *** getStackTraceElement(...);
  *** getStackTraceDepth(...);
}

# FinalizableReferenceQueue calls this reflectively
# Proguard is intelligent enough to spot the use of reflection onto this, so we
# only need to keep the names, and allow it to be stripped out if
# FinalizableReferenceQueue is unused.
-keepnames class com.google.common.base.internal.Finalizer {
  *** startFinalizer(...);
}
# However, it cannot "spot" that this method needs to be kept IF the class is.
-keepclassmembers class com.google.common.base.internal.Finalizer {
  *** startFinalizer(...);
}
-keepnames class com.google.common.base.FinalizableReference {
  void finalizeReferent();
}
-keepclassmembers class com.google.common.base.FinalizableReference {
  void finalizeReferent();
}

# Striped64, LittleEndianByteArray, UnsignedBytes, AbstractFuture
-dontwarn sun.misc.Unsafe

# Striped64 appears to make some assumptions about object layout that
# really might not be safe. This should be investigated.
-keepclassmembers class com.google.common.cache.Striped64 {
  *** base;
  *** busy;
}
-keepclassmembers class com.google.common.cache.Striped64$Cell {
  <fields>;
}

-dontwarn java.lang.SafeVarargs

-keep class java.lang.Throwable {
  *** addSuppressed(...);
}

# Futures.getChecked, in both of its variants, is incompatible with proguard.

# Used by AtomicReferenceFieldUpdater and sun.misc.Unsafe
-keepclassmembers class com.google.common.util.concurrent.AbstractFuture** {
  *** waiters;
  *** value;
  *** listeners;
  *** thread;
  *** next;
}
-keepclassmembers class com.google.common.util.concurrent.AtomicDouble {
  *** value;
}
-keepclassmembers class com.google.common.util.concurrent.AggregateFutureState {
  *** remaining;
  *** seenExceptions;
}

# Since Unsafe is using the field offsets of these inner classes, we don't want
# to have class merging or similar tricks applied to these classes and their
# fields. It's safe to allow obfuscation, since the by-name references are
# already preserved in the -keep statement above.
-keep,allowshrinking,allowobfuscation class com.google.common.util.concurrent.AbstractFuture** {
  <fields>;
}

# Futures.getChecked (which often won't work with Proguard anyway) uses this. It
# has a fallback, but again, don't use Futures.getChecked on Android regardless.
-dontwarn java.lang.ClassValue

# MoreExecutors references AppEngine
-dontnote com.google.appengine.api.ThreadManager
-keep class com.google.appengine.api.ThreadManager {
  static *** currentRequestThreadFactory(...);
}
-dontnote com.google.apphosting.api.ApiProxy
-keep class com.google.apphosting.api.ApiProxy {
  static *** getCurrentEnvironment (...);
}

# Project specific

# Custom rules to ignore warnings from Google Guava
-dontwarn com.google.common.collect.**
-dontwarn com.google.common.base.Converter
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.util.concurrent.**
-dontwarn com.google.common.eventbus.Subscriber
-dontwarn com.google.common.eventbus.SubscriberRegistry
-dontwarn com.google.common.hash.**

# Ignore warnings from Kotlinx Coroutines
-dontwarn kotlinx.coroutines.**

# Ignore warnings from Netty
-dontwarn io.netty.**

# Ignore warnings from Javax Activation
-dontwarn javax.activation.**

# Ignore warrnings from Javax mail
-dontwarn javax.mail.**

# Ignore warnings from Apache
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**

# Fix runtime error when using io.github.classgraph
-keep class io.github.classgraph.** { *; }
-keep class nonapi.** { *; }
-keepclassmembers class nonapi.io.github.classgraph.classloaderhandler.* {
    boolean canHandle(java.lang.Class, nonapi.io.github.classgraph.utils.LogNode);
}

# Keep all classes and methods in com.fasterxml.jackson package
-keep class com.fasterxml.jackson.** { *; }

# Keep all classes and methods in com.fasterxml.jackson.databind package
-keep class com.fasterxml.jackson.databind.** { *; }

# Keep all classes and methods in com.fasterxml.jackson.module.kotlin package
-keep class com.fasterxml.jackson.module.kotlin.** { *; }

# Keep all classes and methods in com.fasterxml.jackson.databind.cfg package
-keep class com.fasterxml.jackson.databind.cfg.** { *; }

# Keep all classes and methods in com.github.victools.jsonschema.generator package
-keep class com.github.victools.jsonschema.generator.** { *; }

# Keep all classes and methods in com.github.victools.jsonschema.generator.Option
-keep class com.github.victools.jsonschema.generator.Option { *; }

# Entry point to the app.
-keep class com.example.ApplicationKt { *; }
