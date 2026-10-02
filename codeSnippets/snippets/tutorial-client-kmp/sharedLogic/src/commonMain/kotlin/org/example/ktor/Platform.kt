package org.example.ktor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform