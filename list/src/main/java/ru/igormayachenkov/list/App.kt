package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val TAG = "myapp.App"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class App : Application() {

    val listRepository  = ListRepository()
    val itemsRepository = ItemsRepository()

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        // Init
        Database.open(this)
    }

}