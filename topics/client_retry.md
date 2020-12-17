[//]: # (title: Retrying failed requests)

## Intro

Every client code doing HTTP requests is facing network errors from time to time.
Usually such errors could be eliminated by retrying the request.
Sometimes, remote web services may be temporarily unavailable for short periods
of time or produce error HTTP status codes that could be easily recovered by doing
a retry.

## Retry feature

Ktor provides optional feature `Retry` that does retry on I/O errors and unsuccessful HTTP status codes.

This feature provides ability to recover from network errors
identified as `IOException` and `HttpRequestTimeoutException` and `UnresolvedAddressException`.
It also may recover from request validation that usually produce `ResponseException`
on a non-successful response status code.

The number of retries is limited by `retries` property.
After every failed attempt it does a fixed time delay configured by `retryIntervalInSeconds`.

When all attempts exceeded, `RequestRetriesExceededException` exception is thrown
having a list of all errors in the suppressed list and a cause.


Please note that HTTP status codes are checked by the request validation feature.
Once you disable it, the retry feature will **not** retry such responses anymore.

Remember that there are always network delays and, due to the network architecture,
 sometimes there is absolutely
no way to identify, if a request was delivered to the server or not.
Therefore, retrying a "failed" request may lead to double requests.
There are cases when double requests are not acceptable. For example, a request
for doing payments or changing some state.
In this case this feature is not suitable and a different retry approach should
be used, or a double requests prevention mechanism should be applied like using
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
