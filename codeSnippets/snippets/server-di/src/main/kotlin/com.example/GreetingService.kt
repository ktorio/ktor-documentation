package com.example

interface GreetingService {
    fun greet(name: String): String
}

class GreetingServiceImpl : GreetingService {
    override fun greet(name: String): String = "Hello, $name!"
}