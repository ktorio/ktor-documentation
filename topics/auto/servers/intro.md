[//]: # (title: Server introduction)
[//]: # (category: servers)
[//]: # (permalink: /servers/index.html)
[//]: # (caption: Server Applications)
[//]: # (ktor_version_review: 1.2.1)


## Features

A _feature_ is a piece of specific functionality that could be plugged into an application. It usually _intercepts_
requests and responses and does its particular functionality.
For example, the [Default Headers](/servers/features/default-headers.html) feature intercepts responses
and appends `Date` and `Server` headers. A feature can be installed into an application using the `install` function
like this: