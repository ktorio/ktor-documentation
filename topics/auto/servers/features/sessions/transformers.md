[//]: # (title: Transformers)
[//]: # (caption: Session Transformers)
[//]: # (category: servers)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/sessions/transformers.html: - /features/sessions/transformers.html)
[//]: # (ktor_version_review: 1.0.0)







## Standard Transformers

### SessionTransportTransformerDigest
{id="SessionTransportTransformerDigest"}

The `SessionTransportTransformerEncrypt` provides a session transport transformer that includes
a hash of the payload with a salt and verifies it. It uses `SHA-256` as the default
hashing algorithm, but it can be changed. It doesn't encrypt the payload, but still without the salt people
shouldn't be able to change it.

```kotlin
// REMEMBER! Change this string and store them safely
val salt = "my unity salt string"
cookie<TestUserSession>(cookieName) {
    transform(SessionTransportTransformerDigest(salt))
}
```

When using this mode, you can send the actual content of the session to the client as a cookie or a header, and as either
raw or transformed.

This mode is considered to be "serverless", since you don't need to store anything on the server side and it is only
the responsibility of the client to store and keep that session. This simplifies the backend, but has security
implications that you need to know, to work in this mode.

In this mode you just call `header` or `cookie` methods inside the `install(Sessions)` block with a single argument
with the name of the cookie or the header.

Inside `header` or `cookie` blocks you have the option to call a `transform` method that allows you to transform
the value sent, for example to authenticate or encrypt it.

```kotlin
install(Sessions) {
    val secretHashKey = hex("6819b57a326945c1968f45236589")

    cookie<SampleSession>("SESSION_FEATURE_SESSION") {
        cookie.path = "/"
        transform(SessionTransportTransformerMessageAuthentication(secretHashKey, "HmacSHA256"))
    }
}
```

* Serving a session without transform allows people to see the contents of the session clearly and then to modify it.
* Serving a session with an Authentication transform means people can see the contents, but it prevents them from modifying it as long
  as you keep your secret hash key safe and use a secure algorithm. It is also possible to use old session strings to go back
  to a previous state.
* Serving a session with an Encrypt transform prevents people from determining the actual contents and modifying it,
  but it is still vulnerable to exploitation and being returned to previous states.
{ .note.security }
  
It is possible to store a timestamp or a nonce encryption and authentication, but you will have to limit the
session time or verify it at the server, reducing the benefits of this mode.

So as a rule of thumb you can use this mode only **if it is not a security concern that people could use old
session states**. And if you are using a session to log in the user, **make sure that you are at least authenticating
the session with a transform**, or people will be able to easily access other people's contents.

Also have it in mind that if your secure key is compromised, a person with the key will be able to generate any
session payload and can potentially impersonate anyone.

It is important to note that changing the key will invalidate all the sessions from all the users.

### SessionTransportTransformerMessageAuthentication
{id="SessionTransportTransformerMessageAuthentication"}

The `SessionTransportTransformerMessageAuthentication` provides a session transport transformer that includes
an authenticated hash of the payload and verifies it. It is similar to SessionTransportTransformerDigest
but uses a HMAC. It uses `HmacSHA265` as the default authentication algorithm, but it can be changed.
It doesn't encrypt the payload, but still without the key people shouldn't be able to change it.

```kotlin
// REMEMBER! Change this string and store them safely
val key = hex("03515606058610610561058")
cookie<TestUserSession>(cookieName) {
    transform(SessionTransportTransformerMessageAuthentication(key))
}
``` 

### SessionTransportTransformerEncrypt
{id="SessionTransportTransformerEncrypt"}

The `SessionTransportTransformerEncrypt` provides a session transport transformer that encrypts the payload
and authenticates it. By default it uses `AES` and `HmacSHA256`, but you can configure it. It requires 
an encryption key and an authentication key compatible in size with the algorithms: 

```kotlin
// REMEMBER! Change ALL the digits in those hex numbers and store them safely
val secretEncryptKey = hex("00112233445566778899aabbccddeeff") 
val secretAuthKey = hex("02030405060708090a0b0c")
cookie<TestUserSession>(cookieName) {
    transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretAuthKey))
}
``` 

## Custom transport transformers
{id="extending-transport-transformers"}

The Sessions API provides a `SessionTransportTransformer` interface, that looks like this:

```kotlin
interface SessionTransportTransformer {
    fun transformRead(transportValue: String): String?
    fun transformWrite(transportValue: String): String
}
```

You can use these transformations to encrypt, authenticate, or transform the Payload.
You have to implement that interface and add the transformer as usual:

```kotlin
cookie<MySession>("NAME") {
    transform(MtSessionTransformer)
}
```

`SessionTransportTransformer` allows to transform the value that is transferred along the request. Since it is
composable, it can has as input either the transported value or a transformation of it. It is composed by two methods:
One that applies the transformation (`transformWrite`) and other that will unapply it (`transformRead`).
The input and the output are Strings in both cases.
Normally `transformWrite` should always work, while `transformRead` might fail if the input is malformed or invalid in
which cases it will return null. 

```kotlin
interface SessionTransportTransformer {
    fun transformRead(transportValue: String): String?
    fun transformWrite(transportValue: String): String
}
``` 