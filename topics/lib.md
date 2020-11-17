<chunk id="install_feature">

To install the `%feature_name%` feature, pass it to the `install` function in the application initialization code. This can be the `main` function ...

```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
  install(%feature_name%)
  // ...
}
```

... or a specified [module](Modules.md):

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(%feature_name%)
    // ...
}
```

</chunk>



<chunk id="add_ktor_artifact">
<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample">
            implementation "io.ktor:%artifact_name%:$ktor_version"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("io.ktor:%artifact_name%:$ktor_version")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample">
        <![CDATA[
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>%artifact_name%</artifactId>
            <version>${ktor_version}</version>
        </dependency>
        ]]>
        </code>
   </tab>
</tabs>
</chunk>


<chunk id="add_artifact">
<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample">
            implementation "%group_id%:%artifact_name%:$%version%"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("%group_id%:%artifact_name%:$%version%")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample">
        <![CDATA[
        <dependency>
            <groupId>%group_id%</groupId>
            <artifactId>%artifact_name%</artifactId>
            <version>${%version%}</version>
        </dependency>
        ]]>
        </code>
   </tab>
</tabs>
</chunk>
