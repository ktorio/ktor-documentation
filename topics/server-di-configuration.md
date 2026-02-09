[//]: # (title: Configure the DI plugin)

<show-structure for="chapter" depth="2"/>

You can configure the dependency injection (DI) plugin in your application configuration file. These settings affect 
the behavior of dependency resolution globally and apply to all registered dependencies.

### Dependency key mapping

The `ktor.di.keyMapping` property defines how dependency keys are generalized and matched during resolution. This
determines which registered dependencies are considered compatible when resolving a requested type.

```yaml
ktor:
  di:
    keyMapping: Supertypes * Nullables * OutTypeArgumentsSupertypes * RawTypes
```

The above example matches the default key mapping used by the DI plugin.

#### Available key mapping options

<deflist>
<def>
<title><code>Default</code></title>
Uses the default combination:
<code-block>Supertypes * Nullables * OutTypeArgumentsSupertypes * RawTypes</code-block>
</def>
<def>
<title><code>Supertypes</code></title>
Allows resolving a dependency using any of its supertypes.
</def>
<def>
<title><code>Nullables</code></title>
Allows matching nullable and non-nullable variants of a type.
</def>
<def>
<title><code>OutTypeArgumentsSupertypes</code></title>
Allows covariance on <code>out</code> type parameters.
</def>
<def>
<title><code>RawTypes</code></title>
Allows resolving generic types without considering type arguments.
</def>
<def>
<title><code>Unnamed</code></title>
Ignores dependency names (<code>@Named</code>) when matching.
</def>
</deflist>

#### Combine key mapping options

You can combine key mapping options using set operators `*` (intersection), `+` (union) and `()` (grouping).

In the following example, a dependency registered as `List<String>` can be resolved as `Collection<String>` (`Supertypes`),
`List` or `List?` (`RawTypes` and `Nullables`):

```yaml
ktor:
  di:
    keyMapping: Supertypes + (Nullables * RawTypes)
```

It will not resolve as `Collection?`, because that combination is not included in the expression

### Conflict resolution policy

The `ktor.di.conflictPolicy` property controls how the DI container behaves when multiple providers are registered for
the same dependency key:

```yaml
ktor:
  di:
    conflictPolicy: Default
```

#### Available policies

<deflist>
<def>
<title><code>Default</code></title>
Throws an exception when a conflicting dependency is declared
</def>
<def>
<title><code>OverridePrevious</code></title>
Overrides the previous dependency with the newly provided one.
</def>
<def>
<title><code>IgnoreConflicts</code></title>
In test environments, the DI plugin uses <code>IgnoreConflicts</code> by default. This allows test code to override
production dependencies without triggering errors.
</def>
</deflist>