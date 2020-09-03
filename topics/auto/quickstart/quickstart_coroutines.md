[//]: # (title: Coroutines)
[//]: # (category: quickstart)
[//]: # (permalink: /quickstart/coroutines.html)
[//]: # (caption: Coroutines)
[//]: # (redirect_from: redirect_from)
[//]: # (- /advanced/kotlinx.coroutines.html: - /advanced/kotlinx.coroutines.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor makes heavy use of Kotlin 1.3 stable coroutines.

Coroutines are a basic Kotlin mechanism (also called `suspend` functions), that among other things allows to do asynchronous programming that is linearly written like plain code
instead of the traditional callback-based approach.

Other modern languages expose a similar, but more specific mechanism called await-async. The Kotlin approach is more generic and flexible and it is less verbose and less error-prone
since the default behaviour when calling an asynchronous (`suspend`) function is to suspend the caller too.

Ktor uses a standard library from JetBrains called [kotlinx.coroutines](/kotlinx/coroutines.html).

Since Ktor is fully asynchronous and intensively uses coroutines, it is a good idea to familiarize yourself with those concepts.