package com.example

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.*
import kotlin.test.*

class GreetingTest {
    @Test
    fun testGreeting() = testApplication {
        application {
            // Override dependencies
            dependencies.provide<GreetingService> { FakeGreetingService() }
            dependencies.provide<UserRepository> { FakeUserRepository() }

            module(
                greetingService = dependencies.resolve<GreetingService>(),
                userRepository = dependencies.resolve<UserRepository>(),
            )
        }

        val response = client.get("/greet/Test")
        assertEquals("Fake greeting", response.bodyAsText())
    }
}

class FakeGreetingService : GreetingService {
    override fun greet(name: String) = "Fake greeting"
}

class FakeUserRepository : UserRepository(FakeDatabase())

class FakeDatabase : Database