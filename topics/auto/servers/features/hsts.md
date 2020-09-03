[//]: # (title: HSTS)
[//]: # (caption: Enable HTTP Strict Transport Security)
[//]: # (keywords: hsts https ssl secure)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/hsts.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.HSTS)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/hsts.html: - /features/hsts.html)
[//]: # (ktor_version_review: 1.0.0)

This feature will add the required _HTTP Strict Transport Security_ headers to the request according to the [RFC 6797](https://tools.ietf.org/html/rfc6797).

>HSTS policy headers are ignored over an insecure HTTP connection. For HSTS to take effect, it should be
>served over a secure (https) connection.
>
{type="note"} 

When the browser receives HSTS policy headers, it will no longer attempt to connect to the server with insecure connections 
for the given period of time. 


## Usage

```kotlin
fun Application.main() {
  // ...
  install(HSTS) 
  // ...
}
```

The code above installs HSTS with the default configuration.  

## Configuration

* `maxAge` (default is 1 year): duration to tell the client to keep the host in a list of known HSTS hosts
* `includeSubDomains` (default is true): adds includeSubDomains directive, which applies this policy to this domain and any subdomains
* `preload` (default is false): consents that the policy allows including the domain into web browser [preloading list](https://https.cio.gov/hsts/#hsts-preloading) 
* `customDirectives` (default is empty): any custom directives supported by specific user-agent