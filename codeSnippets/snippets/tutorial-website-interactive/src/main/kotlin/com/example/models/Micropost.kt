package com.example.models

import java.util.concurrent.atomic.AtomicInteger

class Micropost
private constructor(val id: Int, val headline: String, val body: String) {
    companion object {
        private val idCounter = AtomicInteger()

        fun newEntry(headline: String, body: String) =
            Micropost(idCounter.getAndIncrement(), headline, body)
    }
}

val microposts = mutableListOf(Micropost.newEntry(
    "The drive to develop!",
    "...it's what keeps me going."
))
