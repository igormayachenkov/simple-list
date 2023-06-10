package ru.igormayachenkov.list.data

import org.json.JSONObject

data class Settings(
    val useFab   : Boolean = true, // Floating Add Button
    val useAdd   : Boolean = false, // Normal Add Button
    val useCheckedColor: Boolean = true,
    val sortOrder: SortOrder = SortOrder.NameAsc
){
    constructor(json:JSONObject):this(
        useFab = json.getBoolean("useFab"),
        useAdd = json.getBoolean("useAdd"),
        useCheckedColor = json.getBoolean("useCheckedColor"),
        sortOrder = SortOrder.valueOf(json.getString("sortOrder"))
    )

    fun toJson():JSONObject{
        val json = JSONObject()
        json.put("useFab", useFab)
        json.put("useAdd", useAdd)
        json.put("useCheckedColor", useAdd)
        json.put("sortOrder", sortOrder.name)
        return json
    }
}