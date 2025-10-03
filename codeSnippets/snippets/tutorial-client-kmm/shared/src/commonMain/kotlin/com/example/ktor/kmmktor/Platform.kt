package com.example.ktor.kmmktor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform