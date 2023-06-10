package ru.igormayachenkov.list.data

import org.json.JSONObject

data class Settings(
    val useFab   : Boolean, // Floating Add Button
    val useAdd   : Boolean, // Normal Add Button
    val useCheckedColor: Boolean
){
    companion object {
        fun default() = Settings(
            useFab   = true,
            useAdd   = false,
            useCheckedColor = true
        )
    }

    constructor(json:JSONObject):this(
        useFab = json.getBoolean("useFab"),
        useAdd = json.getBoolean("useAdd"),
        useCheckedColor = json.getBoolean("useCheckedColor")
    )

    fun toJson():JSONObject{
        val json = JSONObject()
        json.put("useFab", useFab)
        json.put("useAdd", useAdd)
        json.put("useCheckedColor", useAdd)
        return json
    }
}