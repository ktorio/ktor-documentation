[//]: # (title: Client plugins)

<link-summary>
Learn how to use client plugins to add common functionality, such as logging, serialization, and authorization.
</link-summary>

Many applications require common functionality that is not part of the core application logic, such as
[logging](client-logging.md), [serialization](client-serialization.md), or [authorization](client-auth.md). In Ktor,
this functionality is provided by client _plugins_.

## Add plugin dependency {id="plugin-dependency"}

Some plugins require an additional [dependency](client-dependencies.md). For example, to use the [Logging](client-logging.md) plugin, you need to add the
`ktor-client-logging` artifact in your build script:

<var name="artifact_name" value="ktor-client-logging"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

Each plugin’s documentation specifies any required dependencies.

## Install a plugin {id="install"}

To install a plugin, pass it to the `install` function inside a [client configuration block](client-create-and-configure.md#configure-client).

For example, installing the `Logging` plugin looks as follows:

```kotlin
```
{src="snippets/_misc_client/InstallLoggingPlugin.kt" include-lines="1-7"}

### Install or replace a plugin {id="install_or_replace"}

In some cases, a plugin may already be installed — for example, by shared client configuration code. In such cases, you
can replace its configuration using `installOrReplace()`:

```kotlin
```
{src="snippets/_misc_client/InstallOrReplacePlugin.kt" include-symbol="client"}

This function installs the plugin if it is not present or replaces its existing configuration if it has already been
installed.

## Configure a plugin {id="configure_plugin"}

Most plugins expose configuration options that can be set inside the `install` block.

For example, the [Logging](client-logging.md) plugin allows you to specify the logger, logging level, and condition for filtering log
messages:

```kotlin
```
{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" include-symbol="client"}


## Create a custom plugin {id="custom"}

If the existing plugins do not meet your needs, you can create your own custom client plugins. Custom plugins allow you
to intercept requests and responses and implement reusable behavior.

To learn more, see [](client-custom-plugins.md).
