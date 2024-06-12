package com.nmd.eventCalendar.shared

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.nmd.eventCalendar.model.Event
import java.lang.reflect.Type

class EventDeserializer : JsonDeserializer<Event> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Event {
        val jsonObject = json?.asJsonObject
        val date = jsonObject?.get("date")?.asString ?: ""
        val name = jsonObject?.get("name")?.asString ?: ""
        val backgroundHexColor = jsonObject?.get("backgroundHexColor")?.asString ?: ""
        val data = jsonObject?.get("data")
        val dataType = jsonObject?.get("dataType")?.asString ?: ""

        val dataClass = Class.forName(dataType)
        val dataTypeType: Type = dataClass as Type
        val dataObject = context?.deserialize<Any>(data, dataTypeType)

        return Event(date, name, backgroundHexColor, dataObject)
    }

}