package ru.igormayachenkov.list.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.IOException
import java.util.*

////////////////////////////////////////////////////////////////////////////////////////////////
// DATA OBJECT: List
data class List(
        val id      : Long,
        var name    : String,
        //var syncState = 0
        var description: String?
) {
    companion object {
        const val TAG = "myapp.List"
    }

    // DATA
    val liveItems = MutableLiveData<ArrayList<Item>>()
    //val items = ArrayList<Item>()
    //var isLoaded: Boolean = false

    fun load() {
        Log.d(TAG, "load")
        if (liveItems.value==null)
            reloadItems()
    }

    private fun reloadItems(){
        liveItems.value = Database.loadListItems(id)
    }

    fun rename(newName: String) {
        Database.updateListName(id, newName)
        name = newName
    }

    fun addItem(name: String?, description: String?) {
        Log.d(TAG, "List.addItem")
        Database.addItem(id, name, description)
        // Reload items
        reloadItems()
    }

    fun updateItemName(itemIndex: Int, name: String?, description: String?) {
        Log.d(TAG, "List.updateItemName")
        liveItems.value?.let { items->
            // Get item
            val (id1) = items[itemIndex]
            // Update item
            Database.updateItemName(id1, name, description)
            // Reload items
            reloadItems()
        }?: run {
            Log.e(TAG,"updateItemName: list is not loaded")
        }
    }

    fun deleteItem(itemIndex: Int) {
        Log.d(TAG, "List.deleteItem")
        liveItems.value?.let { items ->
            // Get item
            val (id1) = items[itemIndex]
            // Delete item
            Database.deleteItem(id1)
            // Reload items
            items.removeAt(itemIndex)
        }?: run {
            Log.e(TAG,"deleteItem: list is not loaded")
        }
    }

    // EXPORT TO A FILE
    @Throws(IOException::class)
    fun toXML(): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<list>\n")
        sb.append("<id>$id</id>\n")
        sb.append("<name>$name</name>\n")
        if (description != null && !description!!.isEmpty()) sb.append("<description>$description</description>\n")
        sb.append("<items>\n")
        liveItems.value?.let { items ->
            for (item in items) {
                sb.append("<item>\n")
                sb.append("""<id>${item.id}</id>""".trimIndent())
                sb.append("""
    <name>${item.name}</name>

    """.trimIndent())
                if (item.description != null && !item.description!!.isEmpty()) sb.append("""
    <description>${item.description}</description>

    """.trimIndent())
                sb.append("""
    <state>${item.state}</state>

    """.trimIndent())
                sb.append("</item>\n")
            }
        }?: run {
            Log.e(TAG,"list is not loaded")
        }
        sb.append("</items>\n")
        sb.append("</list>")
        return sb.toString()
    }

    @Throws(IOException::class)
    fun saveXML(bw: BufferedWriter) {
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        bw.newLine()
        bw.write("<list>")
        bw.newLine()
        bw.write("<id>$id</id>")
        bw.newLine()
        bw.write("<name>$name</name>")
        bw.newLine()
        if (description != null && !description!!.isEmpty()) bw.write("<description>$description</description>")
        bw.newLine()
        bw.write("<items>")
        bw.newLine()
        liveItems.value?.let { items ->
            for (item in items) {
                bw.write("<item>")
                bw.newLine()
                bw.write("<id>" + item.id + "</id>")
                bw.newLine()
                bw.write("<name>" + item.name + "</name>")
                bw.newLine()
                if (item.description != null && !item.description!!.isEmpty()) bw.write("<description>" + item.description + "</description>")
                bw.newLine()
                bw.write("<state>" + item.state + "</state>")
                bw.newLine()
                bw.write("</item>")
                bw.newLine()
            }
        }?: run {
            Log.e(TAG,"list is not loaded")
        }
        bw.write("</items>")
        bw.newLine()
        bw.write("</list>")
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        if (description != null && !description!!.isEmpty()) json.put("description", description)
        // ITEMS
        load()
        val jsItems = JSONArray()
        liveItems.value?.let { items ->
            for (item in items) {
                val js = JSONObject()
                js.put("id", item.id)
                js.put("name", item.name)
                if (item.description != null && !item.description!!.isEmpty()) js.put("description", item.description)
                js.put("state", item.state)
                jsItems.put(js)
            }
        }?: run {
            Log.e(TAG,"list is not loaded")
        }
        json.put("items", jsItems)
        return json
    }


}