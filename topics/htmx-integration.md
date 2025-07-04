[//]: # (title: HTMX integration)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-htmx</code>, <code>io.ktor:ktor-htmx-html</code>,
<code>io.ktor:ktor-server-htmx</code>
</p>
<var name="example_name" value="htmx-integration"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

[HTMX](https://htmx.org/) is a lightweight JavaScript library that enables dynamic client-side behavior using HTML attributes. It supports
features such as AJAX, CSS transitions, WebSockets, and Server-Sent Events — without writing JavaScript.

Ktor provides experimental, first-class support for HTMX through a set of shared modules that simplify integration in
both server and client contexts. These modules offer tools for working with HTMX headers, defining HTML attributes
using Kotlin DSLs, and handling HTMX-specific routing logic on the server.

## Modules overview

Ktor’s HTMX support is available across three experimental modules:

| Module             | Description                                |
|--------------------|--------------------------------------------|
| `ktor-htmx`        | Core definitions and header constants      |
| `ktor-htmx-html`   | Integration with the Kotlin HTML DSL       |
| `ktor-server-htmx` | Routing support for HTMX-specific requests |

All APIs are marked with `@ExperimentalKtorApi` and require opt-in via `@OptIn(ExperimentalKtorApi::class)`.

## HTMX Headers

You can use predefined constants from the core `ktor-htmx` module to access or set HTMX header in a type-safe way.
These constants help you avoid magic strings when detecting HTMX behavior like triggers, history restoration, or
content swapping.

### Request headers

Use the `HxRequestHeaders` object to read or match HTMX request headers in your application:

<deflist type="wide">
<def title="HxRequestHeaders.Request">Always <code>true</code> for HTMX requests</def>
<def title="HxRequestHeaders.Target">ID of the target element</def>
<def title="HxRequestHeaders.Trigger">ID of the triggered element</def>
<def title="HxRequestHeaders.TriggerName">Name of the triggered element</def>
<def title="HxRequestHeaders.Boosted">Indicates request via hx-boost</def>
<def title="HxRequestHeaders.CurrentUrl">Current browser URL</def>
<def title="HxRequestHeaders.HistoryRestoreRequest">For history restoration</def>
<def title="HxRequestHeaders.Prompt">User response to hx-prompt</def>
</deflist>

### Response headers

You can use the `HxResponseHeaders` object to access constants for HTMX response headers:

<deflist type="wide">
<def title="HxResponseHeaders.Location">Client-side redirect without page reload</def>
<def title="HxResponseHeaders.PushUrl">Push URL to history stack</def>
<def title="HxResponseHeaders.Redirect">Client-side redirect</def>
<def title="HxResponseHeaders.Refresh">Force full page refresh</def>
<def title="HxResponseHeaders.ReplaceUrl">Replace current URL</def>
<def title="HxResponseHeaders.Reswap">Control how response is swapped</def>
<def title="HxResponseHeaders.Retarget">Update target of content update</def>
<def title="HxResponseHeaders.Reselect">Choose part of response to swap</def>
<def title="HxResponseHeaders.Trigger">Trigger client-side events</def>
<def title="HxResponseHeaders.TriggerAfterSettle">Trigger events after settle</def>
<def title="HxResponseHeaders.TriggerAfterSwap">Trigger events after swap</def>
</deflist>

## Swap modes

You can use the `HxSwap` object from the core `ktor-htmx` module to access constants for different HTMX swap modes.

<deflist type="medium">
<def title="HxSwap.innerHtml">Replace inner HTML (default)</def>
<def title="HxSwap.outerHtml ">Replace entire element</def>
<def title="HxSwap.textContent">Replace text content only</def>
<def title="HxSwap.beforeBegin">Insert before target element</def>
<def title="HxSwap.afterBegin">Insert as first child</def>
<def title="HxSwap.beforeEnd">Insert as last child</def>
<def title="HxSwap.afterEnd">Insert after target element</def>
<def title="HxSwap.delete">Delete target element</def>
<def title="HxSwap.none">No content append</def>
</deflist>

## HTML DSL extensions

The `ktor-htmx-html` module adds extension functions to Kotlin’s HTML DSL, allowing you to add HTMX attributes directly
to HTML elements:

```kotlin
@OptIn(ExperimentalKtorApi::class)
html {
    body {
        button {
            attributes.hx {
                get = "/data"
                target = "#result"
                swap = HxSwap.outerHtml
                trigger = "click"
            }
            +"Load Data"
        }
    }
}
```

The above example generates HTML with HTMX attributes:

```html
<button hx-get="/api/data" hx-target="#result-div" hx-swap="outerHTML" hx-trigger="click">Load Data</button>
```

## Server-side routing

The `ktor-server-htmx` module provides HTMX-aware routing through the `hx` DSL block:

```kotlin
@OptIn(ExperimentalKtorApi::class)
routing {
    route("api") {
        // Regular route (both HTMX and non-HTMX requests)
        get {
            call.respondText("Regular response")
        }
        
        // Only matches HTMX requests (HX-Request header is present)
        hx.get {
            call.respondText("HTMX response")
        }
        
        // Matches HTMX requests with specific target
        hx {
            target("#result-div") {
                get {
                    call.respondText("Response for #result-div")
                }
            }
            
            // Matches HTMX requests with specific trigger
            trigger("#load-button") {
                get {
                    call.respondText("Response for #load-button clicks")
                }
            }
        }
    }
}
```

These features allow your application to respond differently depending on the HTMX headers sent by the client.
