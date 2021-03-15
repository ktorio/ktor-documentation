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
{interpolate-variables="true"}

... or a specified [module](Modules.md):

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(%feature_name%)
    // ...
}
```
{interpolate-variables="true"}

</chunk>


<chunk id="add_ktor_artifact_intro">

To enable `%feature_name%` support, you need to include the `%artifact_name%` artifact in the build script:

</chunk>


<chunk id="add_ktor_artifact">
<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample" interpolate-variables="true">
            implementation "io.ktor:%artifact_name%:$ktor_version"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample" interpolate-variables="true">
            implementation("io.ktor:%artifact_name%:$ktor_version")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample" interpolate-variables="true">
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


<chunk id="outdated_warning">

> This help topic is in development and will be updated in the future.
> 
{type="note"}

</chunk>

<chunk id="experimental">

The API is production ready, but may be slightly modified in a minor release. This is why it's marked with the
`@%annotation_name%` annotation. If you want to use this API you need to `OptIn`:
```kotlin
@OptIn(%annotation_name%::class)
```
{interpolate-variables="true"}
If you want to leave your feedback or subscribe on updates, check
[KTOR-%issue_number%](https://youtrack.jetbrains.com/issue/KTOR-%issue_number%) design issue.

</chunk>



<chunk id="new_project_idea">
<p>
On the Welcome screen, click <control>New Project</control>.
</p>
<p>
Otherwise, from the main menu, select <menupath>File | New | Project</menupath>.
</p>
</chunk>



<chunk id="download_example">
<p>
Code example: <a href="https://github.com/ktorio/ktor-documentation/tree/master/codeSnippets/snippets/%example_name%">%example_name%</a>
</p>
</chunk>
