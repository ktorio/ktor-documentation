package com.example.dao

import com.example.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseSingleton {
    companion object {
        fun init() {
            val driverClassName = "org.h2.Driver"
            val jdbcURL = "jdbc:h2:file:./build/db"
            val database = Database.connect(jdbcURL, driverClassName)
            transaction(database) {
                SchemaUtils.create(Articles)
            }
        }
    
        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}
