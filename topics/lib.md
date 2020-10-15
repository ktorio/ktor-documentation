## Install %feature_name% {id="install_feature"}
To install the `%feature_name%` feature, pass it to the `install` function in the application initialization code. This can be the `main` function ...
```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
  install(%feature_name%)
  // ...
}
```
... or a specified [module](Modules.md):
```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(%feature_name%)
    // ...
}
```