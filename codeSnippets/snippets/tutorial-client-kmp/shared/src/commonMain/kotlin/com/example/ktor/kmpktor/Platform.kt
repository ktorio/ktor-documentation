package com.example.ktor.kmpktor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform