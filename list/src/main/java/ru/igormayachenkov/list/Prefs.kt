package ru.igormayachenkov.list

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject

//----------------------------------------------------------------------------------------------
// SHARED PREFERENCES WRAPPER
class Prefs(filename:String) {
    var pref : SharedPreferences =
            App.context.getSharedPreferences(filename, Context.MODE_PRIVATE)

    companion object{
        const val TAG = "myapp.Prefs"
        // COMMON KEY LIST
        const val OPEN_LIST_ID      = "open_list_id"
        const val OPEN_ITEM_ID      = "open_item_id"
        //const val OPEN_ITEM_CHANGES = "open_item_changes"
    }

    fun remove(key:String){
        if(pref.contains(key))
        with (pref.edit()) {
            remove(key)
            apply()
        }
    }

    fun saveLong(key:String, value:Long?){
        with (pref.edit()) {
            value?.let {
                putLong(key, it)
            }?: kotlin.run {
                remove(key)
            }
            apply()
        }
    }
    fun loadLong(key:String):Long?{
        return if(pref.contains(key))
            pref.getLong(key, 0)
        else
            null
    }

    fun saveJSON(key:String, value:JSONObject?){
        with (pref.edit()) {
            value?.let { json->
                try {
                    putString(key, json.toString())
                }catch (e:Exception){
                    Log.e(TAG, e.toString())
                }
            }?: kotlin.run {
                remove(key)
            }
            apply()
        }
    }

    fun loadJSON(key:String):JSONObject?{
        pref.getString(key,null)?.let { str->
            try {
                return JSONObject(str)
            }catch (e:Exception){
                Log.e(TAG, e.toString())
            }
        }
        return null
    }
}