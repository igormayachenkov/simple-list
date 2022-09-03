package ru.igormayachenkov.list.settings

import android.util.Log
import ru.igormayachenkov.list.Prefs


object Settings {
    const val TAG = "myapp.Settings"

    val pref : Prefs = Prefs("settings")

    //----------------------------------------------------------------------------------------------
    // INIT (RESTORE)
    init {
        Log.d(TAG, "init")
    }
}