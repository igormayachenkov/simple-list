package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val TAG = "myapp.App"

val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

private lateinit var instance:App
val app:App get() = instance

class App : Application() {

    private val prefs       by lazy { Prefs(prefsDataStore) }
    val settingsRepository  by lazy { SettingsRepository(prefs) }
    val listRepository      by lazy { ListRepository(prefs) }
    val itemsRepository     by lazy { ItemsRepository() }
    val infoRepository      by lazy { InfoRepository() }
    val saverRepository     by lazy { SaverRepository() }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        instance = this

        // Init
        Database.open(this)
    }

}