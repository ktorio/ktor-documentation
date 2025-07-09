package com.example

import com.example.model.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlin.coroutines.CoroutineContext

class PizzaShopImpl : PizzaShop {
    /*
    override suspend fun orderPizza(pizza: Pizza): Receipt {
        return Receipt(7.89)
    }
    */
    private val openOrders = mutableMapOf<String, MutableList<Pizza>>()

    override suspend fun orderPizza(clientID: String, pizza: Pizza): Receipt {
        if(openOrders.containsKey(clientID)) {
            openOrders[clientID]?.add(pizza)
        } else {
            openOrders[clientID] = mutableListOf(pizza)
        }
        return Receipt(3.45)
    }

    override fun viewOrders(clientID: String): Flow<Pizza> {
        val orders = openOrders[clientID]
        if (orders != null) {
            return flow {
                for (order in orders) {
                    emit(order)
                    delay(1000)
                }
            }
        }
        return flow {}
    }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
        println("Server running")
    }.start(wait = true)
}

fun Application.module() {
    install(Krpc)

    routing {
        rpc("/pizza") {
            rpcConfig {
                serialization {
                    json()
                }
            }

            registerService<PizzaShop> { PizzaShopImpl() }
        }
    }
}
