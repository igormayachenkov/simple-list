package ru.igormayachenkov.list

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import ru.igormayachenkov.list.data.Data

////////////////////////////////////////////////////////////////////////////////////////////////////
// THE APPLICATION INSTANCE
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        // Load data
        Data.load(this)
    }

    val packageInfo: PackageInfo?
        get() {
            val pInfo: PackageInfo? = null
            try {
                return packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

    companion object {
        private var instance: App? = null
        @JvmStatic
        fun instance(): App? {
            return instance
        }

        fun context(): Context? {
            return instance //.getApplicationContext();
        }

        fun prefs(): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context())
        }
    }
}