[//]: # (title: Webjars)

<include src="lib.xml" include-id="outdated_warning"/>

The `Webjars` plugin enables serving static content provided by [webjars](https://www.webjars.org/). It allows you to package your assets such as javascript libraries and css as part of your uber-jar.

## Add dependencies {id="add_dependencies"}
<var name="plugin_name" value="Webjars"/>
<var name="artifact_name" value="ktor-webjars"/>
<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install Webjars {id="install_plugin"}

<var name="plugin_name" value="Webjars"/>
<include src="lib.xml" include-id="install_plugin"/>


## Configure Webjars {id="configure"}

```kotlin
    install(Webjars) {
        path = "assets" //defaults to /webjars
        zone = ZoneId.of("EST") //defaults to ZoneId.systemDefault()
    }
```

This configures the plugin to serve any webjars assets on the `/assets/` path. The `zone` argument configures the correct time zone to
be used with the `Last-Modified` header to support caching (only if [Conditional Headers](conditional_headers.md) plugin is also installed).



## Versioning support

Webjars allow developers to change the versions of the dependencies without requiring a change on the path used to load them on your templates.

Let's assume you have imported `org.webjars:jquery:3.2.1`, you can use the following html code to import it:

```html
<head>
  <script src="/webjars/jquery/jquery.js"></script>
</head>  
```

You don't need to specify a version, should you choose to update your dependencies you don't need to modify your templates.