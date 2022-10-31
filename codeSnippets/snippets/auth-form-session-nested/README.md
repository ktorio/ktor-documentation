# Nested authentication providers

This is the modified version of the [auth-form-session](../auth-form-session) sample that 
demonstrates how to use nested authentication providers to add additional protection to specific resources.
In this sample, a user can get access to the `/admin` route only by obtaining a session identifier and 
then logging in by using the `basic` authentication provider.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :auth-form-session-nested:run
```

Then, perform the following steps:
* Open [http://localhost:8080/login](http://localhost:8080/login) and enter the `jetbrains`/`foobar` credentials in a login page to see a greeting and visit count.
* Open [http://localhost:8080/admin](http://localhost:8080/admin) and enter the `admin`/`password` credentials.
