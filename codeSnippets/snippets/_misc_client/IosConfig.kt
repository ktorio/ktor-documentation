val client = HttpClient(Ios) {
    /**
     * Configure native NSUrlRequest.
     */
    configureRequest { // this: NSMutableURLRequest
        setAllowsCellularAccess(true)
        // ...
    }
}