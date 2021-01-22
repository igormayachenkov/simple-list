package ru.igormayachenkov.list.data

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.igormayachenkov.list.App
import java.util.*

///////////////////////////////////////////////////////////////////////////////////////////////
// MAIN DATA OBJECT
object Data {
    const val TAG = "myapp.Data"

    //--------------------------------------------------------------------------
    // Prefs change listerner
    var mPrefChangeListener: OnSharedPreferenceChangeListener? = null

    //--------------------------------------------------------------------------
    // Data objects
    val listOfLists = ListOfLists()

    // LOAD
    fun load(context: Context) {
        listOfLists.load()

        // Create OnSharedPreferenceChangeListener
        mPrefChangeListener = OnSharedPreferenceChangeListener { sp, key -> onSharedPreferenceChanged(sp, key) }
        // Listern for changes
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        //sp.registerOnSharedPreferenceChangeListener(mPrefChangeListener)
        Log.d(TAG, "load finished, size: ${listOfLists.aLL.size}")
    }

    // TO JSON
    @Throws(JSONException::class)
    fun toJSON(lists: Collection<List>): JSONObject {
        val json = JSONObject()
        // Version
        val pInfo = App.instance()?.packageInfo
        if (pInfo != null) {
            json.put("versionCode", pInfo.versionCode)
            json.put("versionName", pInfo.versionName)
        }

        // LISTS
        val listsJSON = JSONArray()
        json.put("lists", listsJSON)
        for (list in lists) {
            listsJSON.put(list.toJSON())
        }
        return json
    }

    // LOAD FROM JSON
    class LoadEstimation {
        @JvmField
        var toInsert = 0
        @JvmField
        var toUpdate = 0
    }

    @Throws(JSONException::class)
    fun estimateLoadingFromJSON(json: JSONObject): LoadEstimation {
        val estimation = LoadEstimation()
        // Version

        // LISTS
        val lists = json.optJSONArray("lists")
        if (lists != null) {
            for (i in 0 until lists.length()) {
                val listJSON = lists.optJSONObject(i) ?: continue

                // Verify list
                val id = listJSON.optLong("id", 0)
                val name = listJSON.optString("name", null)
                if (id == 0L || name == null || name.isEmpty()) continue

                // Search for existed
                val list = listOfLists.getList(id)

                // Update counters
                if (list != null) estimation.toUpdate++ else estimation.toInsert++
            }
        }
        return estimation
    }

    @Throws(JSONException::class)
    fun loadFromJSON(json: JSONObject) {
        // Version

        // LISTS
        val listsJSON = json.optJSONArray("lists")
        if (listsJSON != null) {
            for (i in 0 until listsJSON.length()) {
                val listJSON = listsJSON.optJSONObject(i) ?: continue

                // Verify list
                val id = listJSON.optLong("id", 0)
                val name = listJSON.optString("name", null)
                if (id == 0L || name == null || name.isEmpty()) continue

                // Search for existed
                var list = listOfLists.getList(id)

                // UPDATE DATA
                if (list != null) {
                    // Remove old list (& items)
                    listOfLists.deleteList(id)
                }
                // Insert list
                list = List(id, name, null)
                Database.insertList(list)
                listOfLists.addList(list)
                // Insert list items
                val itemsJSON = listJSON.optJSONArray("items")
                if (itemsJSON != null) {
                    for (j in 0 until itemsJSON.length()) {
                        val itemJSON = itemsJSON.optJSONObject(j) ?: continue
                        val itemName = itemJSON.optString("name", null) ?: continue
                        // Update the database only because of the list is not loaded
                        Database.insertItem(
                                Item.create(
                                        list.id,
                                        itemName,
                                        itemJSON.optString("description", null)
                                )
                        )
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // HANDLERS
    private fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
        Log.d(TAG, "onSharedPreferenceChanged key:$key")
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // DATA OBJECT: List of lists
    class ListOfLists {
        private var isLoaded = false
        private val hashMap: HashMap<Long, List>
        fun getList(id: Long): List? {
            return hashMap[id]
        }

        val aLL: Collection<List>
            get() = hashMap.values

        fun load() {
            Log.d(TAG, "ListOfLists.load")
            if (isLoaded) return
            Database.loadListOfLists(hashMap)
            isLoaded = true
        }

        fun reload() {
            Log.d(TAG, "ListOfLists.reload")
            // Unload
            isLoaded = false
            // Load
            load()
        }

        // Add a new list
        fun addList(list: List): List {
            // Add to the hashmap
            hashMap[list.id] = list
            return list
        }

        // Delete list
        fun deleteList(id: Long) {
            // Remove from the hashMap
            hashMap.remove(id)
        }

        @Throws(JSONException::class)
        fun toJSON(): JSONArray {
            val json = JSONArray()
            for (list in hashMap.values) {
                json.put(list.toJSON())
            }
            return json
        }

        init {
            hashMap = HashMap()
        }
    }

    //---------------------------------------------------------------------------------------------
    // GLOBAL CONSTANTS
    const val ACTIVITY = "ACTIVITY"

}