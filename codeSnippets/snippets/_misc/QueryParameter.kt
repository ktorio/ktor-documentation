get("/products") {
    if (call.request.queryParameters["price"] == "asc") {
        // Show products from the lowest price to the highest
    }
}