package com.nmd.eventCalendar.shared

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.nmd.eventCalendar.model.Event
import java.lang.reflect.Type

class EventSerializer : JsonSerializer<Event> {
    override fun serialize(
        src: Event?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("date", src?.date)
        jsonObject.addProperty("name", src?.name)
        jsonObject.addProperty("backgroundHexColor", src?.backgroundHexColor)
        jsonObject.add("data", context?.serialize(src?.data))
        return jsonObject
    }
}