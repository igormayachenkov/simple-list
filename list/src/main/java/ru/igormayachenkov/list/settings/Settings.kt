package ru.igormayachenkov.list.settings

import android.content.Context
import android.util.Log
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import ru.igormayachenkov.list.App

object Settings {
    private val TAG:String="myapp.Settings"

    val mainColNumber = MutableLiveData<Int>()
    fun set_mainColNumber(value:Int){
        with (getSharedPreferences().edit()) {
            putInt("mainColNumber", value)
            apply()
        }
        mainColNumber.value = value
    }

    // Get SHARED PREFERENCES
    // файлы настроек хранятся в каталоге /data/data/имя_пакета/shared_prefs/имя_файла_настроек.xml
    fun getSharedPreferences():SharedPreferences = App.context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    init {
        Log.d(TAG,"init")

        getSharedPreferences().let { pref->
            mainColNumber.value = pref.getInt("mainColNumber", 2)
        }

    }

}