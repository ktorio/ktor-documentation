package com.example.kmmktor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform