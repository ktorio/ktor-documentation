post("/customer") {
    val customer = call.receive<Customer>()
}