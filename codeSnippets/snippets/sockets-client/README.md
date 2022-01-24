# Client sockets

A sample project demonstrating how to use [TCP/UDP sockets](https://ktor.io/docs/servers-raw-sockets.html) on the client side.

## Running

Before running this sample, start a server application by executing the following command in a repository's root directory:
```bash
./gradlew :sockets-server:run
```

Then, execute the following command to run the client sample:
```bash
./gradlew :sockets-client:run -q --console=plain
```

In a console, enter your name to get a personal greeting:
```Bash
Please enter your name
JetBrains
Hello, JetBrains!
```
