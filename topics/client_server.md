[//]: # (title: Client/Server)

In Ktor, you can manage the [session](sessions.md) data in two ways:
- Pass session data between the server and client. In this case, you can specify how to [serialize](serializers.md) and [transform](transformers.md) the payload to sign or encrypt data sent to the client.
- Store session data on the server and pass only a session ID between the server and client. In such case, you can choose [where to store the payload](storages.md) on the server. For example, you can store session data in memory, in a specified folder, or Redis. If necessary, you can implement your own custom storage.


