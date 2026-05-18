package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

data class ThymeleafUser(val id: Int, val name: String)
