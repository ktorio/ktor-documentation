package com.example.models

import org.jetbrains.exposed.sql.*
import java.io.Serializable

data class Article(val id: Int, val title: String, val body: String): Serializable

object Articles : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 128)
    val body = varchar("body", 1024)

    override val primaryKey = PrimaryKey(id)
}
