[//]: # (title: Retrying failed requests)

## Intro

Every client code doing HTTP requests is facing network errors from time to time.
Usually these errors could be eliminated by retries of the request.
Sometimes remote web services may temporarily be unavailable for short periods of time, or product HTTP status codes that could easily be handled via retries.

A typical code performing retries may look like this:

```
loop@do {
    try {
        return client.get<String>("$myService/url")
    } catch (cause: Throwable) {
        when (cause) {
          is IOException,
          is HttpRequestTimeoutException -> continue@loop
          else -> throw cause
        }
    }
}
```

Applying this approach to every request requires too much effort and looks terribly clumsy.

## Retry feature

Fortunately, Ktor provides an optional `Retry` feature that performs retry on I/O errors and unsuccessful HTTP status codes.

This feature provides the ability to recover from network errors
identified as `IOException`, and `HttpRequestTimeoutException` and `UnresolvedAddressException`.
It also may recover from request validation that usually produce `ResponseException`
on a non-successful response status code.

The number of retries is limited by the `retries` property.
After a failed attempt, it performs a delay for a fixed amount of time configured by the `retryIntervalInSeconds` property.

When all attempts have been exceeded, a `RequestRetriesExceededException` exception is thrown
having a list of all errors in the suppressed list and a cause.


Please note that HTTP status codes are checked by the request validation feature.
Once you disable it, the retry feature will **not** retry such responses anymore.

It's important to note that there can always be network delays. Often due to the network architecture, there is no way to identify if a request has been delivered to the server or not.
Therefore retrying a failed request may lead to duplicate requests.
Duplicate requests are not acceptable in some cases. For example, a request
for doing payments or changing some state. So a different retry approach should
be used, or a duplicate requests prevention mechanism should be applied like using
transaction id and tracking them on server side.

## Usage

The `Retry` feature requires dependency `io.ktor:ktor-client-retry:$ktorVersion`:

```
commonMain {
  dependencies {
    implementation "io.ktor:ktor-client-retry:$ktorVersion"
  }
}
```

The following snippet installs the feature and configures it to make
at most 3 attempts to recover with the attempt interval of 5 seconds.

```
HttpClient {
    retry {
        retries = 3
        retryIntervalInSeconds = 5
    }
}
```

Once the feature is installed, a configured client could be used as usual. You may optionally handle a retry error like this

```
try {
    client.get<String>(someUrl)
} catch (cause: Retry.RequestRetriesExceededException) {
    // handle failure
}
```
