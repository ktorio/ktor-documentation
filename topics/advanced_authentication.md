[//]: # (title: Authentication)

<include src="lib.xml" include-id="outdated_warning"/>

Requires reading first [Advanced Pipeline](Pipelines.md).

`Authentication` plugin creates an `AuthenticationPipeline` which is executed right after the `Plugins` phase
in the call pipeline. All authentication protocols such as basic, digest, oauth are implemented as interceptors on `AuthenticationPipeline`.

## Phases

`AuthenticationPipeline` has two phases:

* `CheckAuthentication` – phase for checking if a user is already authenticated before all authentication mechanisms kick in.
* `RequestAuthentication` – phase for authentication mechanisms to plug into.

The subject of the pipeline is an `AuthenticationContext` instance.

## Protocol

* Auth provider interceptor tries to find a `Principal` in the context of the current call.
* If the principal is found, it is returned, and the pipeline is finished.
* If the principal is not found, the provider will add a challenge to `AuthenticationContext`.
* At the end of the pipeline, if there is no principal, we start calling challenges in order.
* Whichever challenge succeeds first wins. 

## Example Flow

* Basic auth examines the `Authorization` header. 
* If it's missing or invalid, or the user is not recognized, a 401 Unauthorized is sent back to the client, and the current call ends.
* The browser displays a login dialog, and after credentials are provided, it makes a new HTTP request with a proper `Authorization` header.
* Basic auth provider examines the new header, extracts credentials, and verifies them. 
* If the credentials are valid, the principal is attached to the call. If not, a 401 is returned to the client again.