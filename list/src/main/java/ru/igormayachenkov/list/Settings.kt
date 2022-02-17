package ru.igormayachenkov.list

import android.util.Log
import androidx.lifecycle.MutableLiveData


object Settings {
    const val TAG = "myapp.Settings"

    val pref : Prefs = Prefs("settings")

    //----------------------------------------------------------------------------------------------
    // SHOW / HIDE
    var isVisible = MutableLiveData<Boolean>()

    //----------------------------------------------------------------------------------------------
    // INIT (RESTORE)
    init {
        Log.d(TAG, "init")
        isVisible.value = false
    }
}