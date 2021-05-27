[//]: # (title: Retrying failed requests)

Every client code making HTTP requests is facing network errors from time to time. Usually these errors could be eliminated by retries of the request. 

Ktor provides the `Retry` feature that performs retry action on I/O errors and unsuccessful HTTP status codes. This feature provides the ability to recover from network errors identified as `IOException`, and `HttpRequestTimeoutException`, and `UnresolvedAddressException`. It also may recover from request validation that usually produces `ResponseException` on a non-successful response status code.


## Add dependencies {id="add_dependencies"}
To use the `Retry` feature, you need to include the `ktor-client-retry` artifact in the build script:
<var name="artifact_name" value="ktor-client-retry"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install and Configure Retry {id="configure_retry"}
The following snippet installs the feature and configures it to make at most 3 attempts to recover with the attempt interval of 5 seconds: 
```kotlin
HttpClient {
    retry {
        retries = 3
        retryIntervalInSeconds = 5
    }
}
```
When all attempts have been exceeded, a `RequestRetriesExceededException` is thrown, having a list of all errors in the suppressed list and a cause.

> Note that HTTP status codes are checked by the [Response Validation](response-validation.md) feature. Once you disable it, the `Retry` feature will not retry such responses anymore.

You may optionally handle a retry error like this:
```kotlin
try {
    client.get<String>(someUrl)
} catch (cause: Retry.RequestRetriesExceededException) {
    // handle failure
}
```

## Duplicate Requests {id="duplicate_requests"}

It's important to note that there can always be network delays. Often due to the network architecture, there is no way to identify if a request has been delivered to the server or not. Therefore, retrying a failed request may lead to duplicate requests. Duplicate requests are not acceptable in some cases. For example, a request for making payments or changing some state. So a different retry approach should be used, or a duplicate requests prevention mechanism should be applied, like using transaction id and tracking them on the server side.