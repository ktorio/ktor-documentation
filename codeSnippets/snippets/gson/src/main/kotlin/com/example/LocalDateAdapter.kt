package com.example

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate

/**
 * This adapter is needed to serialize java.time.LocalDate to json
 */
class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString()) // Serialize as ISO-8601 string
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
        val jsonString = json?.asString
            ?: throw JsonParseException("Expected a non-null value for LocalDate but found null")
        return LocalDate.parse(jsonString)
     }
}