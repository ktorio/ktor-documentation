[//]: # (title: Code style)

<include src="lib.xml" include-id="outdated_warning"/>

## Official code convention

Ktor as well as other official Kotlin libraries use the [official Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

You can use the official coding standard by adding `kotlin.code.style=official` to your `gradle.properties` file.

## With star imports

The Official Coding Conventions don't define what's the recommended way of using imports.
The IntelliJ default is to include star (`*`) imports after importing at least 5 symbols from a package. But in Ktor and other libraries at JetBrains we use and recommend using star imports always.

The rationale behind it is that usually when you include a class, you will probably want to include all the method and property extension declared for that class.
That's specially convenient for operator extension methods.

You can change the import configuration in `Preferences... -> Editor -> Code Style -> Kotlin -> Imports`:

![](code-style-imports.png)