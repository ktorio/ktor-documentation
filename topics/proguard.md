[//]: # (title: Proguard)

<include from="lib.topic" element-id="outdated_warning"/>

If you have some restrictions on your JAR size, for example, when deploying a free application to [heroku](heroku.md),
you can use proguard to shrink it. If you are using gradle, it is pretty straightforward to use the
[`proguard-gradle` plugin](https://www.guardsquare.com/manual/setup/gradle). You only have to remember to keep: your main module method, the EngineMain
class, and the Kotlin reflect classes. You can then fine-tune it as required:


```groovy
buildscript {
    ext.proguard_version = '7.2.0-beta2'
    dependencies {
        classpath "com.guardsquare:proguard-gradle:$proguard_version"
    }
}

task minimizedJar(type: proguard.gradle.ProGuardTask, dependsOn: shadowJar) {
    injars "build/libs/my-application.jar"
    outjars "build/libs/my-application.min.jar"
    
    if (System.getProperty('java.version').startsWith('1.')) {
        libraryjars "${System.getProperty('java.home')}/lib/rt.jar"
    } else {
        libraryjars "${System.getProperty('java.home')}/jmods/java.base.jmod", jarfilter: '!**.jar', filter: '!module-info.class'
    }
    
    printmapping "build/libs/my-application.map"
    ignorewarnings
    dontobfuscate
    dontoptimize
    dontwarn

    def keepClasses = [
            'io.ktor.server.netty.EngineMain', // The EngineMain you use, netty in this case.
            'kotlin.reflect.jvm.internal.**',
            'io.ktor.samples.hello.HelloApplicationKt', // The class containing your module defined in the application.conf
            'kotlin.text.RegexOption'
    ]

    for (keepClass in keepClasses) {
        keep access: 'public', name: keepClass, {
            method access: 'public'
            method access: 'private'
        }
    }
}
```

>You have a full example on: <https://github.com/ktorio/ktor-samples/tree/main/generic/samples/proguard>
>
>
{type="note"}


