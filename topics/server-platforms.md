[//]: # (title: Supported platforms)

The Ktor server can be used on the following platforms supported by Kotlin:
- JVM
- [Native](server-native.md)
   <include from="client-engines.md" element-id="newmm-note"/>
- [JavaScript](https://kotlinlang.org/docs/js-overview.html)
- [WasmJs](https://kotlinlang.org/docs/wasm-overview.html)

The following [targets](https://kotlinlang.org/docs/multiplatform-dsl-reference.html#targets) are supported:

<table>
<tr>
    <td>
        Target platform
    </td>
    <td>
        Target preset
    </td>
</tr>
<tr>
    <td>
        Kotlin/JVM
    </td>
    <td>
        <list>
            <li>
                <code>jvm</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Kotlin/Wasm
    </td>
    <td>
        <list>
            <li>
                <code>wasmJs</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Kotlin/JS
    </td>
    <td>
        <list>
            <li>
                <code>js</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        iOS
    </td>
    <td>
        <list>
            <li>
                <code>iosArm32</code>
            </li>
            <li>
                <code>iosArm64</code>
            </li>
            <li>
                <code>iosX64</code>
            </li>
            <li>
                <code>iosSimulatorArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        watchOS
    </td>
    <td>
        <list>
            <li>
                <code>watchosArm32</code>
            </li>
            <li>
                <code>watchosArm64</code>
            </li>
            <li>
                <code>watchosX86</code>
            </li>
            <li>
                <code>watchosX64</code>
            </li>
            <li>
                <code>watchosSimulatorArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        tvOS
    </td>
    <td>
        <list>
            <li>
                <code>tvosArm64</code>
            </li>
            <li>
                <code>tvosX64</code>
            </li>
            <li>
                <code>tvosSimulatorArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        macOS
    </td>
    <td>
        <list>
            <li>
                <code>macosX64</code>
            </li>
            <li>
                <code>macosArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Linux
    </td>
    <td>
        <list>
            <li>
                <code>linuxX64</code>
            </li>
            <li>
                <code>linuxArm64</code>
            </li>
        </list>
    </td>
</tr>
</table>