package com.example.healthtracker

import androidx.room.TypeConverter
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarTypeConverter{
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    @TypeConverter
    fun calendarToJson(calendar: Calendar?): JsonElement? {
        return calendar?.let { JsonPrimitive(dateFormat.format(it.time)) }
    }

    @TypeConverter
    fun jsonToCalendar(json: JsonElement?): Calendar? {
        return json?.asString?.let { dateString ->
            val cal = Calendar.getInstance()
            cal.time = dateFormat.parse(dateString)
            cal
        }
    }

}