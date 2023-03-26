package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val TAG = "myapp.App"

val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

class App : Application() {

    private val prefs   by lazy { Prefs(prefsDataStore) }
    val listRepository  by lazy { ListRepository(prefs) }
    val itemsRepository by lazy { ItemsRepository() }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        // Init
        Database.open(this)
    }

}