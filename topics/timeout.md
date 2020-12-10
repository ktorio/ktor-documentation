[//]: # (title: Timeout)

<include src="lib.md" include-id="outdated_warning"/>

Timeout allows limiting the time of the request execution or execution steps such as a connection or a TCP packet awaiting. The following timeout types are available:

* __request timeout__ — time-bound of the whole request processing,
* __connect timeout__ — time-bound of the connection establishment,
* __socket timeout__ — time-bound of the interval between any two subsequent packets (read/write timeout).

By default, all these timeouts are infinite and should be explicitly specified when needed. Timeouts could be specified for all requests of a particular client or for a single request.



## Install

Request, connect and socket timeouts could be specified for all requests of a particular client it the configuration specified during the feature installation:

``` kotlin
val client = HttpClient() {
    install(HttpTimeout) {
        // timeout config
        requestTimeoutMillis = 1000
    }
}
```

Besides, all timeouts could also be specified for a single request and so that override high-level values:

``` kotlin
val client = HttpClient() {
    install(HttpTimeout)
}

client.get<String>(url) {
    timeout {
        // timeout config
        requestTimeoutMillis = 5000
    }
}
```

>Be aware that to assign per-request timeout it's still required to have timeout feature installed. In case per-request configuration is specified without installed feature an `IllegalArgumentException` will be thrown with message _"Consider install HttpTimeout feature because request requires it to be installed"_.
>
{type="note"}

In case of timeout an exception will be thrown. The type of exception depends on type of occured timeout: `HttpRequestTimeoutException` in case of request timeout, `HttpConnectTimeoutException` in case of connect timeout and `HttpSocketTimeoutException` in case of socket timeout.

## Platform-specific behaviour

Not all client engines support all timeout types. `Curl` doesn't support socket timeout, `Ios` doesn't support connect timeout and `Js` supports only request timeout.