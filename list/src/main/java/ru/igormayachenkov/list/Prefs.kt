package ru.igormayachenkov.list

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import ru.igormayachenkov.list.data.SavedOpenList
import ru.igormayachenkov.list.data.Settings

private const val TAG = "myapp.Prefs"

private val STACK       = stringPreferencesKey("stack")
private val SETTINGS    = stringPreferencesKey("settings")

class Prefs(
    private val prefsDataStore: DataStore<Preferences>
) : StackDataSource, SettingsDataSource {

    //----------------------------------------------------------------------------------------------
    // StackDataSource implementation: SAVE/RESTORE STACK
    override suspend fun saveStack(stack: List<SavedOpenList>) {
        Log.d(TAG, "saveStack $stack")
        // Prepare data
        val json = JSONArray()
        for (item in stack){
            json.put(JSONArray().apply {
                put(item.id)
                put(item.firstVisibleItemIndex)
            })
        }
        // Save
        prefsDataStore.edit { prefs ->
            prefs[STACK] = json.toString()
        }
    }
    override fun restoreStack():List<SavedOpenList>{
        val prefs: Preferences = runBlocking {
            prefsDataStore.data.first()
        }
        //Log.d(TAG, "restoreStack prefs:$prefs")
        prefs[STACK]?.let {string->
            try {
                val list = ArrayList<SavedOpenList>()
                val json = JSONArray(string)
                Log.d(TAG, "restoreStack str:$json")
                for(i in 0 until json.length()){
                    val item = json.getJSONArray(i)
                    list.add(
                        SavedOpenList(
                        item.getLong(0),
                        item.getInt (1)
                    )
                    )
                }
                Log.d(TAG, "restoreStack list:$list")
                return list
            } catch (ex: Exception) {
                Log.e(TAG, "restoreStack ex:$ex")
            }
        }
        return emptyList()
    }

    //----------------------------------------------------------------------------------------------
    // SettingsDataSource implementation: SAVE/RESTORE SETTINGS
    override suspend fun saveSettings(settings: Settings) {
        Log.d(TAG, "saveSettings $settings")
        // Prepare data
        val json = settings.toJson()
        // Save
        prefsDataStore.edit { prefs ->
            prefs[SETTINGS] = json.toString()
        }
    }

    override fun restoreSettings(): Settings {
        val prefs: Preferences = runBlocking {
            prefsDataStore.data.first()
        }
        prefs[SETTINGS]?.let { string->
            try {
                val json = JSONObject(string)
                Log.d(TAG, "restoreSettings json:$json")
                val settings = Settings(json)
                Log.d(TAG, "restoreSettings settings:$settings")
                return settings
            } catch (ex: Exception) {
                Log.e(TAG, "restoreSettings ex:$ex")
            }
        }
        return Settings.default()
    }
}