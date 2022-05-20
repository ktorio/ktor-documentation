# Server sockets

A sample project demonstrating how to use [TCP/UDP sockets](https://ktor.io/docs/servers-raw-sockets.html) on the server side.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :sockets-server:run
```
Then, you can connect to the running application in one of the following ways:
* Open the terminal and connect to the application using `telnet`:
   ```Bash
   telnet 127.0.0.1 9002
   ```
   Then, enter your name to get a personal greeting:
   ```Bash
   % ≈
   Trying 127.0.0.1...
   Connected to localhost.
   Escape character is '^]'.
   Please enter your name
   JetBrains
   Hello, JetBrains!
   ```
* Run the [sockets-client](../sockets-client) example and repeat the same steps.
