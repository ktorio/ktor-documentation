[//]: # (title: HSTS)

<include src="lib.xml" include-id="outdated_warning"/>

<var name="plugin_name" value="HSTS"/>
<var name="artifact_name" value="ktor-server-hsts"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
</microformat>

The `%plugin_name%` plugin adds the required _HTTP Strict Transport Security_ headers to the request according to the [RFC 6797](https://tools.ietf.org/html/rfc6797).

>HSTS policy headers are ignored over an insecure HTTP connection. For HSTS to take effect, it should be
>served over a secure (https) connection.
>
{type="note"} 

When the browser receives HSTS policy headers, it will no longer attempt to connect to the server with insecure connections 
for the given period of time. 

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

The code above installs `%plugin_name%` with the default configuration.  

## Configuration

* `maxAge` (default is 1 year): duration to tell the client to keep the host in a list of known HSTS hosts
* `includeSubDomains` (default is true): adds includeSubDomains directive, which applies this policy to this domain and any subdomains
* `preload` (default is false): consents that the policy allows including the domain into web browser [preloading list](https://https.cio.gov/hsts/#hsts-preloading) 
* `customDirectives` (default is empty): any custom directives supported by specific user-agent