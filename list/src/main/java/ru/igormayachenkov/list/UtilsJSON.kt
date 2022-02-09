package ru.igormayachenkov.list

import android.graphics.Color
import android.util.Log
import org.json.JSONObject
import org.json.JSONArray

object UtilsJSON {
    private val TAG: String = "myapp.UtilsJSON"

    fun isNotEmpty(json: JSONObject): Boolean {
        return json.keys().hasNext()
    }

    fun jsonOrNullIfEmpty(json: JSONObject): JSONObject? {
        return if (isNotEmpty(json)) json else null
    }

    fun getLongOrNull(json: JSONObject?, name: String): Long? {
        if (json == null) return null
        if (json.isNull(name)) return null;
        return json.getLong(name)
    }

    fun putLong(json: JSONObject, name: String, value: Long) {
        json.put(name, value)
    }

    fun putLongOrNull(json: JSONObject, name: String, value: Long?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value)
    }

    fun getIntOrNull(json: JSONObject?, name: String): Int? {
        if (json == null) return null
        if (json.isNull(name)) return null
        return json.getInt(name)
    }

    fun getIntOrZero(json: JSONObject, name: String): Int {
        if (json.isNull(name)) return 0
        return json.getInt(name)
    }

    fun getIntOrDefault(json: JSONObject?, name: String, default: Int): Int {
        if (json == null) return default
        if (json.isNull(name)) return default
        return json.getInt(name)
    }

    fun putIntOrNull(json: JSONObject, name: String, value: Int?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value)
    }

    fun getDoubleOrNull(json: JSONObject, name: String): Double? {
        if (json.isNull(name)) return null;
        return json.getDouble(name)
    }

    fun putDoubleOrNull(json: JSONObject, name: String, value: Double?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value)
    }

    fun getFloatOrNull(json: JSONObject, name: String): Float? {
        if (json.isNull(name)) return null;
        return json.getDouble(name).toFloat()
    }

    fun putFloatOrNull(json: JSONObject, name: String, value: Float?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value.toDouble())
    }

    fun getStringOrNull(json: JSONObject?, name: String): String? {
        if (json == null || json.isNull(name)) return null;
        return json.getString(name)
    }

    fun putString(json: JSONObject, name: String, value: String) {
        json.put(name, value)
    }

    fun putStringOrNull(json: JSONObject, name: String, value: String?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value)
    }

    fun getJSONObjectOrNull(json: JSONObject, name: String): JSONObject? {
        if (json.isNull(name)) return null;
        return json.getJSONObject(name)
    }

    fun putJSONObjectOrNull(json: JSONObject, name: String, value: JSONObject?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value)
    }

    fun getJSONArrayOrNull(json: JSONObject, name: String): JSONArray? {
        if (json.isNull(name)) return null;
        return json.getJSONArray(name)
    }

    fun putJSONArrayOrNull(json: JSONObject, name: String, value: JSONArray?) {
        if (value == null) json.put(name, JSONObject.NULL)
        else json.put(name, value)
    }


    fun getCssColorOrNull(json: JSONObject?, name: String): Int? {
        try {
            getStringOrNull(json, name)?.let { str ->
                return Color.parseColor(str)
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
        return null
    }

    fun toCssColorString(color:Int):String{
        val a:Int = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        val str = if(a==0xFF) String.format("#%02X%02X%02X",       r,g,b )
                         else String.format("#%02X%02X%02X%02X", a,r,g,b )
        return str
    }
}