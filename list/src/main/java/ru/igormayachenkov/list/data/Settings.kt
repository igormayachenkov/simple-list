package ru.igormayachenkov.list.data

import org.json.JSONObject

data class Settings(
    val useFab   : Boolean,
    val useAdd   : Boolean
){
    companion object {
        fun default() = Settings(
            useFab   = true,
            useAdd   = false
        )
    }

    constructor(json:JSONObject):this(
        useFab = json.getBoolean("useFab"), // Floating Add Button
        useAdd = json.getBoolean("useAdd")  // Normal Add Button
    )

    fun toJson():JSONObject{
        val json = JSONObject()
        json.put("useFab", useFab)
        json.put("useAdd", useAdd)
        return json
    }
}