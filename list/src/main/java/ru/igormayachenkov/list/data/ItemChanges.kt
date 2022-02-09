package ru.igormayachenkov.list.data

import android.util.Log
import org.json.JSONObject
import ru.igormayachenkov.list.Logic


data class ItemChanges(
    val name        : String,
    val description : String,
    val isChecked   : Boolean
){
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("name",        name)
        json.put("description", description)
        json.put("isChecked",   isChecked)
        return json
    }
    // From JSON
    private constructor(json:JSONObject):this(
            json.getString("name"),
            json.getString("description"),
            json.getBoolean("isChecked"),
    )

    companion object{
        const val TAG = "myapp.ItemChanges"
        const val OPEN_ITEM_CHANGES = "open_item_changes" // Prefs KEY

        fun save(changes:ItemChanges){
            try{
                val json = changes.toJSON()
                Logic.pref.saveJSON(OPEN_ITEM_CHANGES, json)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }

        fun load():ItemChanges?{
            Logic.pref.loadJSON(OPEN_ITEM_CHANGES)?.let{
                try {
                    return ItemChanges(it)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                }
            }
            return null
        }

        fun clear(){
            // Clear saved input
            Logic.pref.remove(OPEN_ITEM_CHANGES)
        }
    }

}
