package com.example

data class BlogEntry(val headline: String, val body: String)

val blogEntries = mutableListOf(BlogEntry(
    "The drive to develop!",
    "...it's what keeps me going."
))