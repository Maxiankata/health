package com.example.healthtracker

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarAdapter : JsonSerializer<Calendar>, JsonDeserializer<Calendar> {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun serialize(src: Calendar, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(dateFormat.format(src.time))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Calendar {
        val cal = Calendar.getInstance()
        cal.time = dateFormat.parse(json.asString)
        return cal
    }
}