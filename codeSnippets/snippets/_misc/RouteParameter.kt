get("/user/{login}") {
    if (call.parameters["login"] == "admin") {
        // ...
    }
}